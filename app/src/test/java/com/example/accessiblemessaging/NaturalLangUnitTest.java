package com.example.accessiblemessaging;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class NaturalLangUnitTest {

    private static final String VALID_STRING = "Login was successful";
    private static final String INVALID_STRING = "Invalid login!";

    @InjectMocks
    NaturalLanguageService myObjectUnderTest;

    @Mock
    Context mMockContext;

    @Test
    public void activeTextToSpeech_WithPlaintext() {

        myObjectUnderTest = new NaturalLanguageService(NaturalLanguageService.OUTPUT_STATES.VOICE);
        myObjectUnderTest.initialize();

        // ...when the string is returned from the object under test...
        NotificationWrapper newNotification = new NotificationWrapper("com.instagram.android", "Micheal", "Ciao", false);
        myObjectUnderTest.speak(newNotification, TextToSpeech.QUEUE_FLUSH);

        // ...then the result should be the expected one.
        //assertThat(result, is(VALID_STRING));

    }
}
