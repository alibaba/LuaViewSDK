set -e
set +u

### Avoid recursively calling this script.
if [[ $SF_MASTER_SCRIPT_RUNNING ]]
then
exit 0
fi
set -u
export SF_MASTER_SCRIPT_RUNNING=1

### Constants.
SF_TARGET_NAME=${PROJECT_NAME}
UNIVERSAL_OUTPUTFOLDER=${BUILD_DIR}/${CONFIGURATION}-universal
IPHONE_DEVICE_BUILD_DIR=${BUILD_DIR}/${CONFIGURATION}-iphoneos
IPHONE_SIMULATOR_BUILD_DIR=${BUILD_DIR}/${CONFIGURATION}-iphonesimulator

### Take build target.
if [[ "$SDK_NAME" =~ ([A-Za-z]+) ]]
then
SF_SDK_PLATFORM=${BASH_REMATCH[1]} # iphoneos or iphonesimulator.
else
echo "Could not find platform name from SDK_NAME: $SDK_NAME"
exit 1
fi

### Build simulator platform. (i386, x86_64)
xcodebuild -project "${PROJECT_FILE_PATH}" -target "${TARGET_NAME}" -configuration "${CONFIGURATION}" -sdk iphonesimulator BUILD_DIR="${BUILD_DIR}" OBJROOT="${OBJROOT}" BUILD_ROOT="${BUILD_ROOT}" CONFIGURATION_BUILD_DIR="${IPHONE_SIMULATOR_BUILD_DIR}" SYMROOT="${SYMROOT}" -arch i386 -arch x86_64 ARCHS='i386 x86_64' VALID_ARCHS='i386 x86_64' $ACTION

### Build device platform. (arm64, armv7)
xcodebuild -project "${PROJECT_FILE_PATH}" -target "${TARGET_NAME}" -configuration "${CONFIGURATION}" -sdk iphoneos BUILD_DIR="${BUILD_DIR}" OBJROOT="${OBJROOT}" BUILD_ROOT="${BUILD_ROOT}" CONFIGURATION_BUILD_DIR="${IPHONE_DEVICE_BUILD_DIR}" SYMROOT="${SYMROOT}" -arch armv7 -arch arm64 VALID_ARCHS='armv7 arm64' $ACTION

### Copy the framework structure to the universal folder (clean it first).
rm -rf "${UNIVERSAL_OUTPUTFOLDER}"
mkdir -p "${UNIVERSAL_OUTPUTFOLDER}"

### copy the iphoneos frame structure to the universal folder (clean it first).
cp -R "${BUILD_DIR}/${CONFIGURATION}-iphoneos/lib${PROJECT_NAME}.a" "${UNIVERSAL_OUTPUTFOLDER}/lib${PROJECT_NAME}.a"

### copy include
cp -R "${BUILD_DIR}/${CONFIGURATION}-iphoneos/include" "${UNIVERSAL_OUTPUTFOLDER}/include"


### Smash them together to combine all architectures.
lipo -create  "${BUILD_DIR}/${CONFIGURATION}-iphonesimulator/lib${PROJECT_NAME}.a" "${BUILD_DIR}/${CONFIGURATION}-iphoneos/lib${PROJECT_NAME}.a" -output "${UNIVERSAL_OUTPUTFOLDER}/lib${PROJECT_NAME}.a"

### Open the universal folder.
#open "${UNIVERSAL_OUTPUTFOLDER}"