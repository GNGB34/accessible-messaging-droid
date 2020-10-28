using System;
using System.Diagnostics;
using Firebase.MLKit.Vision;
using Foundation;
using UIKit;

namespace AccessibleMessaging.Services
{
    public class ScreenshotProcessing
    {

        protected VisionApi vision;

        public ScreenshotProcessing()
        {
            vision = VisionApi.Create();
        }

        public void ScreenshotProcessMLKit(UIImage screenshot)
        {
            var textRecognizer = vision.GetOnDeviceTextRecognizer();

            var image = new VisionImage(screenshot);

            Debug.WriteLine("TEST!");
            textRecognizer.ProcessImage(image, HandleVisionTextRecognitionCallback);
        }

        public void HandleVisionTextRecognitionCallback(VisionText text, NSError error)
        {
            Debug.WriteLine("CAPTURED");
            Debug.WriteLine(error?.Description ?? text?.Text);
        }
    }
}
