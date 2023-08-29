package com.msd.unittest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@ExperimentalCoroutinesApi
abstract class CoroutineTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
}
