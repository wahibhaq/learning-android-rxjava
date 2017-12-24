package com.learning.rxjava.introtorxtutorials.part3

import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class AdvancedErrorHandling : BaseRxObs() {

    /**
     * To convert an error into a normal value before terminating successfully with onComplete
     */
    fun onErrorReturn() {
        val values = Observable.create<Int> {
            it.onNext(1)
            it.onNext(2)
            it.onError(Throwable("Unknown Number"))
        }

        disposable.add(values
                .onErrorReturn { _ -> -1 }
                .subscribeWith(intDisposableObserver()))
    }

    val values = Observable.create<String> {
        it.onNext("Just")
        it.onNext("Random")
        it.onNext("Text")
        it.onError(Throwable("weird text!"))
    }

    /**
     * Allows you to resume a failed sequence with another sequence.
     * The error will not even appear in the resulting observable.
     * onExceptionResumeNext() only captures errors that are Exception
     */
    fun onErrorResumeNext() {
        disposable.add(values
                .onErrorResumeNext(Observable.just("weird text is ok"))
                .subscribeWith(stringDisposableObserver()))
    }

    /**
     * retry re-subscribes to the source and emits everything again from the start.
     */
    fun retryInCaseOfError() {
        disposable.add(values
                .retry(3)
                .subscribeWith(stringDisposableObserver()))

    }

    /**
     * If you want to have more control over restart of subscription in case of error.
     *
     */
    fun retryWhenEncounters() {
        values.retryWhen({ t -> t.take(2).delay(2, TimeUnit.SECONDS) })
                .subscribe(DisplayConsumer("RetryWhen"),
                        DisplayConsumer("Fucking Error"))
    }


}