/**
 * @file web.ts
 * @description Web implementation of the DeviceSecurityDetect plugin.
 * This implementation is limited due to the constraints of the web platform.
 */

import { CapacitorException, ExceptionCode, WebPlugin } from '@capacitor/core';
import type { DeviceSecurityDetectPlugin, DeviceSecurityDetectResult } from './definitions';

/**
 * @class DeviceSecurityDetectWeb
 * @extends WebPlugin
 * @implements DeviceSecurityDetectPlugin
 * 
 * Provides a fallback implementation for the web platform, where the functionality of this plugin is not supported.
 */
export class DeviceSecurityDetectWeb extends WebPlugin implements DeviceSecurityDetectPlugin {
  /**
   * Detect if the device is rooted or jailbroken.
   * 
   * @returns A rejected promise indicating the method is unimplemented on the web.
   */
  async isJailBreakOrRooted(): Promise<DeviceSecurityDetectResult> {
    console.warn('DeviceSecurityDetect: Method isJailBreakOrRooted is not supported on the web.');
    throw this.createUnimplementedError('Method isJailBreakOrRooted');
  }

  /**
   * Check if a password or PIN is enabled on the device.
   * 
   * @returns A rejected promise indicating the method is unimplemented on the web.
   */
  async pinCheck(): Promise<DeviceSecurityDetectResult> {
    console.warn('DeviceSecurityDetect: Method pinCheck is not supported on the web.');
    throw this.createUnimplementedError('Method pinCheck');
  }

  /**
   * Check if the device is running on an emulator or simulator.
   * 
   * @returns A rejected promise indicating the method is unimplemented on the web.
   */
  async isSimulator(): Promise<DeviceSecurityDetectResult> {
    console.warn('DeviceSecurityDetect: Method isSimulator is not supported on the web.');
    throw this.createUnimplementedError('Method isSimulator');
  }

  /**
   * Check if the application is running in debug mode.
   * 
   * @returns A rejected promise indicating the method is unimplemented on the web.
   */
  async isDebuggedMode(): Promise<DeviceSecurityDetectResult> {
    console.warn('DeviceSecurityDetect: Method isDebuggedMode is not supported on the web.');
    throw this.createUnimplementedError('Method isDebuggedMode');
  }

  /**
   * Utility method to create an exception for unimplemented functionality.
   * 
   * @returns {CapacitorException} An exception with an `Unimplemented` code and a descriptive message.
   */
  private createUnimplementedError(message: string): CapacitorException {
    return new CapacitorException(
      `${message} This feature requires native platform support.`,
      ExceptionCode.Unimplemented,
    );
  }
}
