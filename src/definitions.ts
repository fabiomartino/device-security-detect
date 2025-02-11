/**
 * @file definitions.ts
 * @description TypeScript type definitions for the DeviceSecurityDetect plugin.
 */

/**
 * @interface DeviceSecurityDetectPlugin
 * @description Defines the methods provided by the DeviceSecurityDetect plugin.
 */
export interface DeviceSecurityDetectPlugin {
  /**
   * Detect if the device has been rooted (Android) or jailbroken (iOS).
   * 
   * This method provides a boolean value indicating whether the device
   * has been tampered with (e.g., by rooting or jailbreaking).
   * 
   * @returns A promise that resolves to an object containing:
   * - `value`: A boolean indicating if the device is rooted or jailbroken.
   * 
   * @example
   * ```typescript
   * const result = await DeviceSecurityDetect.isJailBreakOrRooted();
   * console.log(result.value); // true if rooted/jailbroken, false otherwise
   * ```
   * @since 6.0.0
   */
  isJailBreakOrRooted(): Promise<DeviceSecurityDetectResult>;

  /**
   * Check if a PIN, password, or biometric authentication is enabled on the device.
   * 
   * This method checks whether the user has set up any kind of secure lock mechanism
   * (e.g., PIN, password, or biometric authentication) on their mobile device.
   * 
   * @returns A promise that resolves to an object containing:
   * - `value`: A boolean indicating if secure authentication is enabled.
   * 
   * @example
   * ```typescript
   * const result = await DeviceSecurityDetect.pinCheck();
   * console.log(result.value); // true if PIN/password is enabled, false otherwise
   * ```
   * @since 6.0.2
   */
  pinCheck(): Promise<DeviceSecurityDetectResult>;

  /**
   * Check if the device is running on an emulator or simulator.
   * 
   * This method determines whether the application is running on a virtualized environment
   * such as an Android Emulator or iOS Simulator.
   * 
   * @returns A promise that resolves to an object containing:
   * - `value`: A boolean indicating if the device is an emulator or simulator.
   * 
   * @example
   * ```typescript
   * const result = await DeviceSecurityDetect.isSimulator();
   * console.log(result.value); // true if running on an emulator/simulator, false otherwise
   * ```
   * @since 7.0.1
   */
  isSimulator(): Promise<DeviceSecurityDetectResult>;

  /**
   * Check if the application is running in debug mode.
   * 
   * This method detects whether the app is being debugged, which can indicate potential security risks.
   * 
   * @returns A promise that resolves to an object containing:
   * - `value`: A boolean indicating if the app is in debug mode.
   * 
   * @example
   * ```typescript
   * const result = await DeviceSecurityDetect.isDebuggedMode();
   * console.log(result.value); // true if in debug mode, false otherwise
   * ```
   * @since 7.0.1
   */
  isDebuggedMode(): Promise<DeviceSecurityDetectResult>;
}

/**
 * @interface DeviceSecurityDetectResult
 * @description Represents the result of a security check performed by the plugin.
 */
export interface DeviceSecurityDetectResult {
  /**
   * Indicates the result of the security check.
   * 
   * - `true`: The condition being checked is met (e.g., the device is rooted, an emulator, etc.).
   * - `false`: The condition is not met (e.g., the device is not rooted, not an emulator, etc.).
   */
  value: boolean;
}