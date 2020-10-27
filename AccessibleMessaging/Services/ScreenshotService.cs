using System;
using UIKit;
using System.Runtime.InteropServices;
using Foundation;

namespace AccessibleMessaging.Services
{
    public class ScreenshotService
    {
        public ScreenshotService()
        {

            //TODO - idk if we want to put in our own constructor, etc.
        }

        public byte[] Capture()
        {
            var capture = UIScreen.MainScreen.Capture();
            using (NSData data = capture.AsPNG())
            {
                var bytes = new byte[data.Length];
                Marshal.Copy(data.Bytes, bytes, 0, Convert.ToInt32(data.Length));
                return bytes;
            }
        }
    }
}
