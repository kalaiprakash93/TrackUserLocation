function(isServiceRunning, errorLogger) {
		cordova.exec(isServiceRunning, errorLogger, 'LocationPlugin', 'isServiceRunning', []);
		}
