package io.yoobi.intefaces;

/**
 * Created by GEMVN on 1/25/2018.
 */
public interface TypedMapping<S1, S2>
{
    S2 accept(S1 s1, S2 s2);
}
