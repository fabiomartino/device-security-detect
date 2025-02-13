import { DeviceSecurityDetect } from '@capacitor-community/device-security-detect';
import { SplashScreen } from '@capacitor/splash-screen';

window.onload = async () => {
  SplashScreen.hide();
  await checkSecurityStatus();
};

async function checkSecurityStatus() {
  updateStatus("jailbreakStatus", await DeviceSecurityDetect.isJailBreakOrRooted(), false);
  updateStatus("pinStatus", await DeviceSecurityDetect.pinCheck(), true);
  updateStatus("emulatorStatus", await DeviceSecurityDetect.isSimulator(), false);
  updateStatus("debugStatus", await DeviceSecurityDetect.isDebuggedMode(), false);
}

function updateStatus(elementId, result, isPositive) {
  const element = document.getElementById(elementId);
  const isSuccess = result.value === isPositive;

  const emoji = isSuccess ? "😊" : "😢";
  const text = result.value ? "Yes" : "No";

  element.textContent = `${emoji} ${text}`;
  element.style.backgroundColor = isSuccess ? "green" : "red";
}