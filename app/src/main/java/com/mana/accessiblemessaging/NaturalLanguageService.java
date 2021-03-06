// ==========================================================================
// NaturalLanguageService
// ==========================================================================
// Control linker for Android Text-to-Speech and Firebase MLKit Language
// detection/translation for incoming Notifications
//
// Creator: Gabriel Cordovado (10/11/20)
// Email:   gcord057@uottawa.ca
// ==========================================================================

//TODO - THIS CLASS IS NOT ENTIRELY COMPLETE

package com.mana.accessiblemessaging;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

public class NaturalLanguageService {

    // ---------------- Constant Variables ----------------

    public enum OUTPUT_STATES{DO_NOT_DISTURB, VOICE, VOICE_MINIMAL, STOP};
    public final Locale DEFAULT_LANGUAGE = Locale.US;

    // ---------------- Instance Variables ----------------

    private TextToSpeech controller;
    private OUTPUT_STATES state;
    private Locale language;

    // ---------------- Constructor Class -----------------

    public NaturalLanguageService(OUTPUT_STATES state) {
        this.state = state;
    }

    // ---------------- Encapsulate Classes -----------------

    public void setState(OUTPUT_STATES newState) { state = newState; }
    public OUTPUT_STATES getState() { return state; };

    // ----------------   Builder Class   -----------------

    public void initialize(Context context) {
        controller = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                switch (status) {
                    case TextToSpeech.SUCCESS:
                        controller.setLanguage(DEFAULT_LANGUAGE);
                        break;
                    default:
                        Log.d("VOICE_SERVICE", "Voice Service - Failed to initialize text-to-speech");
                }
            }
        });
    }

    // --------------- Independent Methods ----------------

    public void switchLanguage(Locale newLanguage) {
        int status = controller.setLanguage(newLanguage);
        language = newLanguage;
        switch (status) {
            case TextToSpeech.LANG_MISSING_DATA:
                Log.d("VOICESERVICE", "Voice Service - Language Missing Data : When trying to change language.");
                break;
            case TextToSpeech.LANG_NOT_SUPPORTED:
                Log.d("VOICESERVICE", "Voice Service - Language Not Supported : When trying to change language.");
                break;
            default:
                Log.d("VOICESERVICE", "Voice Service - Successfully Changed language");
        }
    }

    public Locale translateLanguageCode(String languageIdentifier) {
        switch (languageIdentifier) {
            case "en":
                return Locale.US;
            case "fr":
                return Locale.FRENCH;
            case "es":
                return new Locale("es", "ES");
            default:
                //this will handle less-common languages such as Spanish (requested by the client) that do not have pre-set constants defined within
                //the JVM environment -> it is possible that this can crash if we feed in a languageIdentifier not supported
                return new Locale(languageIdentifier);
        }
    }

    private String formatNotification(NotificationWrapper notification) {

        switch (state) {
            case VOICE:
                return String.format("New message from %s on %s. %s", notification.getSender(), notification.getApplication(), notification.getText());
            case VOICE_MINIMAL:
                return String.format("New message from %s on %s", notification.getSender(), notification.getApplication());
            default:
                return null;
        }
    }

    public void speak(NotificationWrapper wrapper, int queueRequest) {

        //If we are in DO NOT DISTURB we don't want anything to read or waste CPU time
        if (state == OUTPUT_STATES.DO_NOT_DISTURB) return;

        int status;
        String formattedPlaintext = formatNotification(wrapper);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            status = controller.speak(formattedPlaintext, queueRequest, null, null);
        }
        else {
            status = controller.speak(formattedPlaintext, queueRequest, null);
        }

        if (status == TextToSpeech.ERROR) {
            Log.e("VOICESERVICE", "Voice Service - Could not convert the plaintext into voice projection");
        }
    }

    private void translatePlaintext(final String sourceLNG, final String targetLNG, NotificationWrapper wrapper) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                                            .setSourceLanguage(sourceLNG)
                                            .setTargetLanguage(targetLNG)
                                            .build();
        final Translator newSourceToTargetTranslator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        newSourceToTargetTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                //TODO - implement on success
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //TODO - implement on failure
                            }
                        }
                );

        newSourceToTargetTranslator.translate(wrapper.getText())
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                if (o != null) {
                                    String translatedText = o.toString();
                                    NotificationWrapper translatedWrapper = new NotificationWrapper(wrapper, translatedText);
                                    speak(translatedWrapper, TextToSpeech.QUEUE_ADD);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //TODO - implement on failure
                            }
                        });
    }

    public void detectLanguageCode(NotificationWrapper wrapper) {
        Log.d("CheckNUll", language == null ? "null": "we good");
        //If we are in DO NOT DISTURB we don't want anything to read or waste CPU time
        if (state == OUTPUT_STATES.DO_NOT_DISTURB) return;

        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(wrapper.getText())
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                Log.d("LAN_CODE", languageCode);

                                if (languageCode.equals("und")) {
                                    Log.d("VOICESERVICE", "Voice Service - Language Not Discoverable : When trying to determine the language.");
                                }
                                else if (language.equals(translateLanguageCode(languageCode))) {
                                    //
                                    speak(wrapper, TextToSpeech.QUEUE_ADD);
                                }
                                else {
                                    //
                                    translatePlaintext(languageCode, language.getLanguage(), wrapper);
                                    Log.d("VOICESERVICE", "Voice Service - Successfully Changed language");
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("VOICESERVICE", "Voice Service - Error Thrown While Tying to determine the language");
                            }
                        });
    }

    // --------------- DeConstructor Methods ----------------

    public void destroy() {
        if (controller != null) return;
        controller.stop();
        controller.shutdown();
    }
}
