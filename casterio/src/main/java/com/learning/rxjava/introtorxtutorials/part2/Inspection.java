package com.learning.rxjava.introtorxtutorials.part2;


import com.learning.rxjava.introtorxtutorials.BaseRxObs;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class Inspection extends BaseRxObs implements InspectionContract {

    private static final String TAG = Inspection.class.getSimpleName();

    public Inspection() {
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

    /** isEmpty() checks if no element in the source exists.
     * if you want to avoid checking, then you can also use defaultIfEmpty()
     */
    @Override
    public void isEmpty() {
        List<String> source = new ArrayList<>();
        Observable<String> values = Observable.fromIterable(source);
        disposable.add(values
                .isEmpty()
                .subscribeWith(boolDisposableSingleObserver()));

    }

    /** if stream contains a particular element or not **/
    @Override
    public void contains() {
        List<String> source = new ArrayList<>(Arrays.asList("AA", "BB", "CC"));
        Observable<String> values = Observable.fromIterable(source);
        disposable.add(values
                .contains("BB")
                .subscribeWith(boolDisposableSingleObserver()));
    }

    /**
     * if two separate streams are equal or not. Both the size of sequence and values must be same.
     */
    @Override
    public void equals() {
        Observable<Integer> wholeNumber = Observable.just(1, 2, 3);
        Observable<Double> decimalNumber = Observable.just(1.1, 2.7, 3.3);

        disposable.add(Observable
        .sequenceEqual(wholeNumber, decimalNumber,
                (whole, decimal) -> whole.equals(decimal.intValue()))
                .subscribeWith(boolDisposableSingleObserver()));

    }

    private boolean isPrime(int number) {
        BigInteger bigInt = BigInteger.valueOf(number);
        return bigInt.isProbablePrime(100);
    }

}
