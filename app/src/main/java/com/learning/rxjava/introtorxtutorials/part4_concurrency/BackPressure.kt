package com.learning.rxjava.introtorxtutorials.part4_concurrency

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.BackpressureOverflowStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


/**
 * Cold Observable: emits a particular sequence of items but can begin emitting this sequence when
 * its Observer finds it to be convenient, and at whatever rate the Observer desires, without
 * disrupting the integrity of the sequence.
 *
 * Hot Observable: emits items at its own pace, and it is up to its observers to keep up.
 *
 * For RxJava2 BackPressure, I followed https://github.com/ReactiveX/RxJava/wiki/Backpressure-(2.0)
 */
class BackPressure : BaseRxObs() {

    private fun compute(v: Int) {
        try {
            Log.i(TAG, "compute integer v: " + v)
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    /**
     * I tried but it is not producing the MissingBackpressureException.
     * Why? I think there are multiple reasons. 1) In the 2.x Observable doesn't do backpressure at all.
     * 2) The simplest solution to fix in RxJava 1.x was to use the onBackpressureBuffer() operator
     * from the ReactiveX library, which is effectively what RxJava 2.x does by default now.
     */
    fun causingBackPressureExceptionOnRx1() {
        val source = PublishSubject.create<Int>()

        source.observeOn(Schedulers.computation())
                .subscribe({ t -> compute(t) }, { t -> t.printStackTrace() })
        Observable.range(1, 100000).forEach({ source.onNext(it) })
    }

    /**
     * For RxJava2 stuff, I had to follow https://github.com/ReactiveX/RxJava/wiki/Backpressure-(2.0)
     *
     *In this example, the main thread will produce 1 million items to an end consumer which is
     * processing it on a background thread. It is likely the compute(int) method takes some time
     * but the overhead of the Flowable operator chain may also add to the time it takes to process
     * items. However, the producing thread with the for loop can't know this and keeps onNexting.
     *
     * Most developers encounter backpressure when their application fails with
     * MissingBackpressureException and the exception usually points to the observeOn() operator.
     * The actual cause is usually the non-backpressured use of PublishProcessor, timer() or
     * interval() or custom operators created via create()
     */
    fun causingBackPressureExOnRx2() {
        val source = PublishProcessor.create<Int>()

        source
                .observeOn(Schedulers.computation())
                .subscribe({ compute(it) }, { it.printStackTrace() })

        for (i in 0..999999) {
            source.onNext(i)
        }

        Thread.sleep(10000)
    }

    /**
     * In this function, there is no error and everything runs smoothly with small memory usage.
     * The reason for this is that many source operators can "generate" values on demand and thus
     * the operator observeOn can tell the range generate at most so many values the observeOn buffer
     * can hold at once without overflow. This negotiation is based on the computer science concept
     * of co-routines (I call you, you call me).
     *
     * Flowable.range(1, 1_000_000)
            .subscribe(new DisposableSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(1); // indicates range to produce its first value
            }

            public void onNext(Integer v) {
                compute(v); //after onStart() value is received here.

                request(1);
            }

            @Override
            public void onError(Throwable ex) {
                ex.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Done!");
            }
            });

        Flowable takes backpressure into consideration. Observable does not.
     */
    fun understandingCoroutines() {
        Flowable.range(1, 1000000)
                .observeOn(Schedulers.computation())
                .subscribe({ v -> compute(v) }, { it.printStackTrace() })

        Thread.sleep(10000)
    }


    /**
     * -> Solutions to handle backpressure on the side of the observer in RxJava1:
     *
     * 1) Thin out the data = use sample(), throttle() or debounce()
     * 2) Buffering overproducing obs = use buffer(), window() or collect(). This is useful if
     * processing items in batches is faster. We can increase a buffer size to have enough room for
     * produced values. Note however that generally, this may be only a temporary fix as the
     * overflow can still happen if the source overproduces the predicted buffer size.
     *
     * There are also Strategies of buffering, batching and skipping elements.
     */


    /**
     * This works because of buffer() and bufferSize in observeOn() operator.
     * If some of the values can be safely ignored, one can also use the sampling (with time or another
     * Flowable) and throttling operators (throttleFirst, throttleLast, throttleWithTimeout).
     *
     * observeOn's has default 16-element internal buffer on Android. Most backpressure-sensitive
     * operators in the recent versions of RxJava now allow programmers to specify the size of their
     * internal buffers. The relevant parameters are usually called bufferSize, prefetch or
     * capacityHint. Given the overflowing example in the introduction, we can just increase the
     * buffer size of observeOn to have enough room for all values.
     * 
     * Just defining bufferSize may be only a temporary fix as the overflow can still happen if the
     * source overproduces the predicted buffer size. In this case, one can use one of the following operators.
     *
     * That's why other batching or sampling techniques are also equally important.
     */
    fun usingBufferToAvoidBackPressure() {
        val source = PublishProcessor.create<Int>()
        source
                .buffer(1024)
                .observeOn(Schedulers.computation(), true, 1024)
                .subscribe {
                    list -> Log.i(TAG, list.parallelStream().map { it -> it * it }.findFirst().toString())

                }

        for(i in 0..1000000) {
            source.onNext(i)
        }
    }

    /**
     * This operator in its parameterless form reintroduces an unbounded buffer between the upstream
     * source and the downstream operator. Being unbounded means as long as the JVM doesn't run out
     * of memory, it can handle almost any amount coming from a bursty source.
     *
     * In this example, the observeOn goes with a very low buffer size yet there is no
     * MissingBackpressureException as onBackpressureBuffer soaks up all the 1 million values and
     * hands over small batches of it to observeOn.
     *
     * range() supports backpressure
     *
     * There are 4 additional overloads of onBackpressureBuffer. onBackpressureBuffer(int capacity)
     * but the relevance of this operator is decreasing as more and more operators now allow setting
     * their buffer sizes.
     */
    fun usingOnBackPressureBuffer() {
        Flowable.range(1, 1_000_000)
                .onBackpressureBuffer()
                .observeOn(Schedulers.computation(), true, 8)
                .subscribe({}, {it.printStackTrace()})
    }

    /**
     * This overload is actually more useful as it let's one define what to do in case the capacity
     * has been reached.
     *
     * DROP_OLDEST: Drop the oldest value from the buffer.
     * DROP_LATEST: Drop the latest value from the buffer.
     */
    fun usingOnBackPressureWithStrategy() {
        Flowable.range(1, 1_000_000)
                .onBackpressureBuffer(2, { },
                        BackpressureOverflowStrategy.DROP_OLDEST)
                .observeOn(Schedulers.computation())
                .subscribe( DisplayConsumer("BackPressureStrategy") ,
                        Consumer<Throwable>{ it.printStackTrace() })
    }

    /**
     * Whenever the downstream is not ready to receive values, this operator will drop that element
     * from the sequence.
     *
     * When to use? This operator is useful when one can safely ignore values from a source (such as
     * mouse moves or current GPS location signals) as there will be more up-to-date values later on.
     */
    fun usingOnBackPressureDrop() {
        Flowable.interval(1, TimeUnit.MINUTES)
                .onBackpressureDrop()
                .observeOn(Schedulers.io())
                .doOnNext { t -> /** apiManager.getSomeThing() **/ }
                .subscribe()
    }

    /**
     * This operator keeps only the latest value and practically overwrites older, undelivered values.
     *
     * When to use? For example, if the user clicks a lot on the screen, we'd still want to react
     * to its latest input.
     */
    fun usingOnBackPressureLatest() {
        Flowable.interval(1, TimeUnit.MINUTES)
                .onBackpressureLatest()
                .observeOn(Schedulers.computation())
                .subscribe({ event -> /**compute(event.x, event.y)**/ },
                        {it.printStackTrace()})
    }

    /**
     * The most basic backpressure aware source is created via just()
     * just() is great when there is a constant value we'd like to jump-start a sequence.
     */
    fun usingJustWithFlowable() {
        disposable.add(
                Flowable.just(10)
                .subscribeWith(intDisposableSubscriber()))
    }

    var counter = 0
    fun computeValue() = ++counter

    fun usingFromCallable() {

    }


}