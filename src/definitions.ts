export interface DeviceSecurityDetectPlugin {

  /**
   * Detect if the device has been rooted (Android) or jailbroken (iOS)
   *
   * @since 6.0.0
   */
  isJailBreakOrRooted(): Promise<{ value: boolean }>;

}
