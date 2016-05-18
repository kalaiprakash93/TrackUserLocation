var exec = require('cordova/exec');

function LocationPlugin() { 
 console.log("LocationPlugin.js: is created");
}

LocationPlugin.prototype.isServiceRunning = function(isServiceRunning, errorLogger){
	exec(isServiceRunning, errorLogger, 'LocationPlugin', 'isServiceRunning', []);
}
 var locationPlugin = new LocationPlugin();
 module.exports = locationPlugin;
