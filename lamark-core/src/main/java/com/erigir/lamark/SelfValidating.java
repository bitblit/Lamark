package com.erigir.lamark;

/**
 * Classes marked SelfValidating will have their validate method called
 * during the Lamark build - they should throw exceptions if anything is
 * wrong with them.
 *
 * Created by cweiss1271 on 2/1/17.
 */
public interface SelfValidating {
    void selfValidate();
}
