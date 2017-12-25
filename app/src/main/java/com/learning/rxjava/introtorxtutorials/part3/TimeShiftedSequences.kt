package com.learning.rxjava.introtorxtutorials.part3

import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit


class TimeShiftedSequences : BaseRxObs() {

    val valuesRange = Observable.range(1, 20)
    val valuesInterval = Observable.interval(1, TimeUnit.SECONDS).take(20)

    //buffer allows you to collect valuesRange and get them in bulks, rather than one at a time.
    // The are several different ways of buffering valuesRange.

    /**
     * The simplest overload groups a fixed number of valuesRange together and emits the group when it's ready.
     */
    fun bufferByCount() {
        disposable.add(
                valuesRange
                        .filter { it -> it % 2 != 0 }
                        .buffer(2)
                        .subscribe(DisplayConsumer("bufferByCount"))
        )
    }

    /**
     * Time is divided into windows of equal length. Values are collected for the each window
     * and at the end of each window the buffer is emitted.
     */
    fun bufferByTime() {
        disposable.add(
                valuesInterval
                        .buffer(3, TimeUnit.SECONDS)
                        .subscribe(DisplayConsumer("bufferByTime"))
        )
    }

    /**
     * Overlapping Buffers: you can also declare how far apart the beginings of each buffer are.
     */
    fun bufferWithSkip() {
        disposable.add(
                valuesRange
                        .buffer(2, 4)
                        .subscribe(DisplayConsumer("bufferWithSkip"))
        )
    }

    /**
     * You can think of it as delaying the beginning of the sequence, while doubling the
     * time intervals between successive elements.
     */
    fun delayEmission() {
        disposable.add(
                valuesInterval
                        .delay { it -> Observable.timer(it * 2, TimeUnit.SECONDS) }
                        .timeInterval()
                        .subscribe(DisplayConsumer("delayEmission"))
        )
    }

    /**
     * Rather than storing values and emitting them later, you can delay the subscription altogether.
     * Almost same effect but more efficient, since the operator doesn't need to buffer items internally.
     */
    fun delaySubscription() {
        disposable.add(
                valuesInterval
                        .delaySubscription(3, TimeUnit.SECONDS)
                        .timeInterval()
                        .subscribe(DisplayConsumer("delaySubscription"))
        )
    }

    /**
     * When you want to extract specific emissions out of a stream after dividing into time windows.
     */
    fun useSample() {
        disposable.add(
                valuesInterval
                        .sample(5, TimeUnit.SECONDS)
                        .subscribeWith(longDisposableObserver())
        )
    }

    //Throttling is also intended for thinning out a sequence. When the producer emits more values
    // than we want and we don't need every sequential value, we can thin out the sequence by throttling it.


    /**
     * The throttleFirst operators filter out values relative to the values that were already accepted.
     *
     * The throttleLast operator divides time at regular intervals, rather than relative to the last
     * item. it emits the last value in each window, rather than the first after it.
     */
    fun throttleFirst() {
        disposable.add(
                valuesInterval
                        .throttleFirst(5, TimeUnit.SECONDS)
                        .subscribeWith(longDisposableObserver())
        )
    }

    val uncertainIncomingRequest = Observable.concat(
            Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
            Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
            Observable.interval(100, TimeUnit.MILLISECONDS).take(3))

    /**
     * We didn't use the interval source because interval observable will either have all of its
     * values accepted or only its last value accepted (which is never if the observable is infinite)
     *
     * There is a throttleWithTimeout operator which has the same behaviour as the debounce operator
     *
     * This operator is useful against observables that undergo periods of uncertainty, where the
     * value changes frequently from one non-definitive state to another. For example, imagine that
     * you are monitoring the contents of a text field and you want to offer suggestions based on
     * what the user is writting. You could recompute your suggestions on every keystroke, but that
     * would be too noisy and too costly. If, instead, you debounce the changes to the text field,
     * you will offer suggestions only when the user has paused or finished typing.
     */
    fun debounceRequest() {

        disposable.add(
                        uncertainIncomingRequest
                        .scan(0, { t1, t2 -> t1 + 1 })
                        .debounce(150, TimeUnit.MILLISECONDS)
                        .subscribeWith(intDisposableObserver())
        )
    }

    /**
     * timeout is used to detect observables that have remained inactive for a given amount of time.
     * If a specified amount of time passes without the source emitting any items, timeout
     * makes the observable fail with a TimeoutException.
     *
     * Instead of failing, you can  also provide a fallback observable like I am doing with -1.
     *
     * You can also specify the timeout window per item. In that case, you provide a function that
     * creates an observable for each value. When the observable terminates, that is the signal for
     * the timeout. If no values had been emitted until that, that triggers the timeout.
     */
    fun timeout() {
        disposable.add(
                uncertainIncomingRequest
                        .scan(0, { t1, t2 -> t1 + 1 })
                        .timeout<Long> ( Function {  Observable.timer(400, TimeUnit.MILLISECONDS)}, Observable.just(-1) )
                        .subscribeWith(intDisposableObserver())

        )
    }

}