// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorCommunityDeviceSecurityDetect",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorCommunityDeviceSecurityDetect",
            targets: ["DeviceSecurityDetectPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "DeviceSecurityDetectPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/DeviceSecurityDetectPlugin"),
        .testTarget(
            name: "DeviceSecurityDetectPluginTests",
            dependencies: ["DeviceSecurityDetectPlugin"],
            path: "ios/Tests/DeviceSecurityDetectPluginTests")
    ]
)