package com.learning.rxjava.introtorxtutorials.part3_taming_sequence

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit

/**
 * All about blocking
 *
 * Common thing about all operators is that They don't return an observable that will emit the
 * value when it is available. Rather, they block until the value is available and return the
 * value itself, without the surrounding observable.
 */
class LeavingMonad : BaseRxObs() {

    /**
     * blockingSubscribe() or blockingForEach() are the equivalent of toBlocking() from RxJava1
     *
     * The difference with normal subscribe() is that blocking one literally blocks the flow and
     * "Subscribed" is only printed once values has printed
     */
    fun toBlocking() {
        val values = Observable.interval(1, TimeUnit.SECONDS)
        values
                .take(10)
                .blockingSubscribe { it -> Log.i(TAG, "toBlocking : " + it) }

        Log.i(TAG, "Subscribed")
    }

    /**
     * In this case, there won't be able display unless 5 seconds have elapsed.
     *
     * similar ways for blockingFirst(), blockingSingle()
     */
    fun blockingLast() {
        val values = Observable.interval(1, TimeUnit.SECONDS)
        val lastItem = values.take(5).blockingLast()

        Log.i(TAG, "Subscribed: " + lastItem)
    }

    /**
     * so like other operators, instead of blockingIterable() in RxJava1, its now blockingIterable()
     *
     * all the emitted values are collected and cached. Because of the caching, no items will be missed.
     * The iterator gets the next value as soon as possible, either immediately if it has already
     * occured, or it blocks until the next value becomes available.
     */
    fun blockingIterable() {
        val values = Observable.interval(1, TimeUnit.SECONDS)
        val resultIterable = values.take(5).blockingIterable()
        for(element: Long in resultIterable) {
            Log.i(TAG, "Iterable : " + element)
        }

        Log.i(TAG, "Subscribed")

    }

    /**
     * with blockingNext() values are not cached at all. The iterator will always wait for the
     * next value and return that.
     *
     * In this example the consumer is slower than the producer and always misses the next value.
     * The iterator gets the next after that.
     */
    fun blockingNext() {
        val values = Observable.interval(1, TimeUnit.SECONDS)
        values
                .take(5)
                .subscribe { it -> Log.i(TAG, "Emitting : " + it) }

        val resultIterable = values.take(5).blockingNext()
        for(element: Long in resultIterable) {
            Log.i(TAG, "Iterable : " + element)
            Thread.sleep(1250)
        }

        Log.i(TAG, "Subscribed End")
    }

    /**
     * The latest method is similar to next, with the difference that it will cache one value.
     * When using the latest iterator, values will be skipped if they are not pulled before
     * the next event is emitted, which also means the latest value is replaced.
     *
     * If you see, 4 was never consumed. That was because an onCompleted followed immediately,
     * resulting in the next pull seeing a terminated observable.
     *
     *
     * blockingMostRecent() is somewhat related. The mostRecent iterator never blocks.
     * It caches a single value, therefore values may be skipped if the consumer is slow.
     * Unlike latest, the last cached value is always returned, resulting in repetitions if the
     * consumer is faster than the producer.
     */
    fun blockingLatest() {
        val values = Observable.interval(1, TimeUnit.SECONDS)
        values
                .take(5)
                .subscribe { it -> Log.i(TAG, "Emitting : " + it) }

        val resultIterable = values.take(5).blockingLatest()
        for(element: Long in resultIterable) {
            Log.i(TAG, "Iterable : " + element)
            Thread.sleep(2250)
        }

        Log.i(TAG, "Subscribed End")
    }

    /**
     * forEach returns only after the termination of the sequence. However, the termination event
     * requires forEach to return before being pushed. Therefore, forEach will never unblock.
     */
    fun makeDeadLock() {
        val subject: ReplaySubject<String> = ReplaySubject.create()
        subject.blockingForEach { DisplayConsumer("deadlock scene") }
        subject.onNext("blah")
        subject.onNext("blah")
        subject.onComplete()
    }

}