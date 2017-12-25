package com.learning.rxjava.introtorxtutorials.part3

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.observables.ConnectableObservable
import java.util.concurrent.TimeUnit


class HotObservables : BaseRxObs() {

    /**
     * The ConnectableObservable will initially emit nothing. When calling connect, it will create
     * a new subscription to its source observable (the one we called publish on). It will begin
     * receiving events and pushing them to its subscribers. All of the subscribers will receive
     * the same events at the same time, as they are practically sharing the same subscription:
     * the one that connect created.
     */
    val connectable: ConnectableObservable<Long> = Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(10)
            .publish()

    fun usingConnect() {
        connectable.connect()

        connectable.subscribe(DisplayConsumer("first"))
        Thread.sleep(500)
        connectable.subscribe(DisplayConsumer("second"))
    }

    fun usingReconnect() {
        connectable.subscribe(DisplayConsumer("usingReconnect"))
        val dispoable = connectable.connect()


        Thread.sleep(1000)
        Log.i(TAG, "Closing Connection")
        dispoable.dispose()

        Thread.sleep(1000)
        Log.i(TAG, "Reconnecting")

        //unlike tutorial, I have to subscribe again to start receiving emittions
        connectable.subscribe(DisplayConsumer("usingReconnect"))
        connectable.connect()
    }

    fun unsubscribingOnlyOne() {
        connectable.connect()

        val disposable1 = connectable.subscribe(DisplayConsumer("First"))
        Thread.sleep(500)
        val disposable2 = connectable.subscribe(DisplayConsumer("Second"))
        Thread.sleep(500)

        Log.i(TAG, "Disconnecting Second")
        disposable2.dispose()
    }

    /**
     * replay resembles the ReplaySubject
     */
    fun replayEmissions() {
        val cold = Observable.interval(200, TimeUnit.MILLISECONDS).replay()
        cold.connect()

        Log.i(TAG, "Subscribe First")
        val disposable1 = cold
                .take(10)
                .subscribe(DisplayConsumer("First"))
        Thread.sleep(500)

        Log.i(TAG, "Subscribe Second")
        val disposable2 = cold
                .take(10)
                .subscribe(DisplayConsumer("Second"))
        Thread.sleep(500)
    }

    /**
     * This one takes 2 as bufferSize and will replay only the last 2 values
     *
     * They are different ways of providing one or more of 3 parameters: bufferSize, selector and time (plus unit for time).
     */
    fun replaceSpecificEmissions() {
        val source = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .take(10)
                .replay(2)

        source.connect()
        Thread.sleep(5000)
        source.subscribe(DisplayConsumer("replaceSpecificEmissions"))
    }

    /**
     * The cache operator has a similar function to replay, but hides away the
     * ConnectableObservable and removes the managing of subscriptions. Subsequent subscribers have
     * the previous values replayed to them from the cache and don't result in a new subscription
     * to the source observable.
     */
    fun cacheEmissions() {
        val source = Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(10)
                .cache()

        source.subscribe(DisplayConsumer("First"))
        Thread.sleep(500)
        source.subscribe(DisplayConsumer("Second"))
        Thread.sleep(300)
    }

}