package com.learning.rxjava.introtorxtutorials.part2_sequence_basics

interface AggregationContract {

    fun count()

    fun first()

    fun single()

    fun reduce()

    fun scan()

    fun collect()

    fun toMap()

    fun toMultiMap()

    fun map()

    fun ofType()

    fun logTimeInterval()

    fun flatMap()

    fun concatMap()
}