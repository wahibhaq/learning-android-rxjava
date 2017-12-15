package com.learning.rxjava.introtorxtutorials;


import java.math.BigInteger;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class Part2Inspection extends BaseRxObs implements InspectionTutorial {

    private static final String TAG = Part2Inspection.class.getSimpleName();

    private final CompositeDisposable disposable = new CompositeDisposable();

    public Part2Inspection() {
    }

    /**
     * The all method establishes that every value emitted by an observable meets a criterion.
     * In this case, checking if all emitted values are prime or not
     */
    @Override
    public void allWithSuccess() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(2);
            o.onNext(3);
            o.onNext(7);
            o.onNext(11);
            o.onComplete();
        });

        //getting if all numbers are prime or not
        disposable.add(values
                .all(this::isPrime)
                .subscribeWith(boolDisposableSingleObserver()));

    }

    private boolean isPrime(int number) {
        BigInteger bigInt = BigInteger.valueOf(number);
        return bigInt.isProbablePrime(100);
    }

    @Override
    public void clear() {
        disposable.clear();
    }
}
