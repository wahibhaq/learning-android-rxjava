package com.learning.rxjava.introtorxtutorials.part2_sequence_basics;


public interface ReducingSeqContract {

    void filter();

    void distinct();

    void take();

    void takePerTime();

    void skip();
}
