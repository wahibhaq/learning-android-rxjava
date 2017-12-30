package com.learning.rxjava.introtorxtutorials.part4_concurrency

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Rx is single-threaded by default.
 * In Rx you don't juggle threads directly. Instead you wrap them in policies called Scheduler.
 */
class SchedulingThreading : BaseRxObs() {

    /**
     * We see here that we called onNext on our subject from 3 different threads. Every time, the
     * subscriber's action was executed on the same thread where the first onNext call came from.
     * The same would be true no matter how many operators we had chained together. The value goes
     * through the chain synchronously, unless we request otherwise.
     */
    fun investigateSingleThreaded() {
        val subject = BehaviorSubject.create<Int>()
        subject.subscribe { i -> Log.i(TAG, "Received " + i + " on " + Thread.currentThread().id) }

        val i = intArrayOf(1) // naughty side-effects for examples only ;)

        val runnable = Runnable {
            synchronized(i) {
                Log.i(TAG, "onNext(" + i[0] + ") on " + Thread.currentThread().id)
                subject.onNext(i[0]++)
            }
        }

        runnable.run() // Execute on main thread
        Thread(runnable).start()
        Thread(runnable).start()
    }

    /**
     * Without using subscrubeOn(), not only everything is executed on main thread but subscribe
     * will also block until it has completed subscribing to (and thus creating) the observable,
     * which includes executing the body of create's lambda parameter.
     */
    fun understandingSubscribeOn() {

        Log.i(TAG, "Main: " + Thread.currentThread().id)

        Observable.create<Int> { o ->
            println("Created on " + Thread.currentThread().id)
            o.onNext(1)
            o.onNext(2)
            o.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ i -> Log.i(TAG, "Received " + i + " on " + Thread.currentThread().id) })

        Log.i(TAG, "Finished main: " + Thread.currentThread().id)
    }

    /**
     * Unlike subscribeOn, observeOn's effect doesn't jump to the start of the pipeline. It just
     * changes the thread for the operators that come after it. You can think of it as intercepting
     * events and changing the thread for the rest of the chain. Here's an example for this.
     *
     * you can assign different threading policies to different parts of an Rx pipeline.
     */
    fun understandingObserverOn() {
        Observable.create<Int> { o ->
            Log.i(TAG, "Created on " + Thread.currentThread().id)
            o.onNext(1)
            o.onNext(2)
            o.onComplete()
        }
                .doOnNext { i -> Log.i(TAG, "Before " + i + " on " + Thread.currentThread().id) }
                .observeOn(Schedulers.newThread())
                .doOnNext { i -> Log.i(TAG, "After " + i + " on " + Thread.currentThread().id) }
                .subscribe()
    }

    /**
     * In exceptional cases, where you need the unsubscription actions to not block or to specifically
     * take place on a special thread, you can specify the scheduler that will execute those actions
     * with unsubscribeOn
     *
     *  using() operator is creating a disposable resource that has the same lifespan as the Observable.
     *  The using method executes 3 functions, one that leases a resource, one that uses it
     *  and one the releases it.
     */
    fun understandingUbsercribeOn() {
        val source = Observable.using(
                {
                    Log.i(TAG,"Subscribed on " + Thread.currentThread().id)
                    Arrays.asList(1, 2)
                },
                { ints ->
                    Log.i(TAG,"Producing on " + Thread.currentThread().id)
                    Observable.fromIterable(ints)
                },
                { _ -> Log.i(TAG,"Unubscribed on " + Thread.currentThread().id) })

        source
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(DisplayConsumer("understandingUbsercribeOn"))
    }

    /**
     * Worker is a Sequential Scheduler for executing actions on a single thread or event loop.
     * The action is queued to be executed on the thread that the worker is assigned to.
     *
     * Scheduler.Worker extends Disposable. Calling the dispose() method on a worker will result
     * in the queue being emptied and all pending tasks being cancelled. That's why this function
     * only displays one value.
     */
    fun useSchedulerWorkder() {
        val scheduler = Schedulers.newThread()
        val start = System.currentTimeMillis()
        val worker = scheduler.createWorker()
        worker.schedule(
                {
                    Log.i(TAG,(System.currentTimeMillis() - start).toString())
                    worker.dispose()
                },
                5, TimeUnit.SECONDS)
        worker.schedule(
                {
                    Log.i(TAG,(System.currentTimeMillis() - start).toString()) },
                5, TimeUnit.SECONDS)
    }

    /**
     * The io.reactivex.Scheduler abstract base class in RxJava2 now supports scheduling tasks
     * directly without the need to create and then destroy a Worker (which is often forgotten)
     *
     * The main purpose is to avoid the tracking overhead of the Workers for typically one-shot
     * tasks. The methods have a default implementation that reuses createWorker properly but can
     * be overridden with more efficient implementations if necessary.
     */
    fun useScheduleDirect() {
        val scheduler = Schedulers.newThread()
        val start = System.currentTimeMillis()
        scheduler.scheduleDirect({
            Log.i(TAG,(System.currentTimeMillis() - start).toString())
        }, 5, TimeUnit.SECONDS)
    }

    /**
     * The TrampolineScheduler's worker executes every task on the thread that scheduled the first
     * task. In this implementation, the first call to schedule is blocking until the queue is
     * emptied. Any calls to schedule while executing will be non-blocking and the task will be
     * executed by the thread is blocked.
     */
    fun trampolineScheduler() {
        val scheduler = Schedulers.trampoline()
        val worker = scheduler.createWorker()
        worker.schedule {
            Log.i(TAG, "Start")
            worker.schedule { Log.i(TAG, "Inner") }
            Log.i(TAG, "End")
        }
    }

    /**
     * The NewThreadScheduler creates workers that each have their own thread. Every scheduled task
     * will be executed on the thread corresponding to that particular worker.
     */
    fun newThreadScheduler() {
        printThread("Main")
        val scheduler = Schedulers.newThread()
        val worker = scheduler.createWorker()
        worker.schedule {
            printThread("Start")
            worker.schedule { printThread("Inner") }
            printThread("End")
        }

        Thread.sleep(500)
        worker.schedule { printThread("Again") }
    }

    fun printThread(message: String) {
        Log.i(TAG,message + " on " + Thread.currentThread().id)
    }
}