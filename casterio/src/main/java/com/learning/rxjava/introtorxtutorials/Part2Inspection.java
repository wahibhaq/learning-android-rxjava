package com.learning.rxjava.introtorxtutorials;


import io.reactivex.disposables.Disposable;

public class Part2Inspection extends BaseRxObs implements InspectionTutorial {

    private static final String TAG = Part2Inspection.class.getSimpleName();

    private Disposable disposable;

    public Part2Inspection() {
    }

    @Override
    public void clear() {
        if(!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
