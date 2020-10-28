using System;
using UIKit;
using AccessibleMessaging.Services;

namespace AccessibleMessaging.TestCases
{
    public class MLKitRecognitionTest
    {
        public MLKitRecognitionTest()
        {
            UIImage image = UIImage.FromFile("Screenshots/test.png");

            ScreenshotProcessing sp;
            sp = new ScreenshotProcessing();

            sp.ScreenshotProcessMLKit(image);
        }
    }
}
