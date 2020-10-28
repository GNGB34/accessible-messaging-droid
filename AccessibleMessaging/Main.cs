using UIKit;
using AccessibleMessaging.TestCases;
using System;

namespace AccessibleMessaging
{
    public class Application
    {
        // This is the main entry point of the application.
        static void Main(string[] args)
        {
            // if you want to use a different Application Delegate class from "AppDelegate"
            // you can specify it here.
            UIApplication.Main(args, null, "AppDelegate");

            Firebase.Core.App.Configure();
            _ = Firebase.MLKit.Common.ModelManager.DefaultInstance;

            Console.WriteLine("This is a test!");

            MLKitRecognitionTest test = new MLKitRecognitionTest(); //TEST CASE ONE
        }
    }
}