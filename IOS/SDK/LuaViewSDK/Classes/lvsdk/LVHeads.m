/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVHeads.h"


@implementation LVHeads

NSString * const LVScriptExts[] = {@"lv", @"lua"};
const int LVSignedScriptExtIndex = 0;

const char* LVType_View = "LVType_UserDataView";
const char* LVType_Data = "LVType_UserDataData";
const char* LVType_Date = "LVType_UserDataDate";
const char* LVType_Http = "LVType_UserDataHttp";
const char* LVType_Timer = "LVType_UserDataTimer";
const char* LVType_Transform3D = "LVType_UserDataTransform3D";
const char* LVType_Animator = "LVType_UserDataAnimator";
const char* LVType_Gesture = "LVType_UserDataGesture";
const char* LVType_Downloader = "LVType_UserDataDownloader";
const char* LVType_AudioPlayer = "LVType_UserDataAudioPlayer";
const char* LVType_StyledString = "LVType_UserDataStyledString";
const char* LVType_NativeObject = "LVType_UserDataNativeObject";
const char* LVType_Struct = "LVType_UserDataStruct";
const char* LVType_Canvas = "LVType_UserDataCanvas";
const char* LVType_Event = "LVType_UserDataEvent";
const char* LVType_Bitmap = "LVType_UserDataBitmap";

@end
