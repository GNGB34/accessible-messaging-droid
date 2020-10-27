using System;
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

            textRecognizer.ProcessImage(image, HandleVisionTextRecognitionCallback);
        }

        public void HandleVisionTextRecognitionCallback(VisionText text, NSError error)
        {
            Console.WriteLine(error?.Description ?? text?.Text);
        }
    }
}
