
function gcm_message_received(message)
{
	//console.info(message)
	chrome.storage.local.get({
		native_port: 58372,
		receive_only: true
	}, function(items) {
		//chrome.runtime.sendNativeMessage(
		//	'org.ucworks.gcmn.host_pipe',
		//	message,
		//	function(response) {
		//		console.log("Received " + response);
		//	});
		var port = chrome.runtime.connectNative('org.ucworks.gcmn.host_pipe')
		// port.onMessage.addListener(function(msg) {
		// console.log('Received ' + msg)
		//})
		port.postMessage({ port: parseInt(items.native_port) })
		port.postMessage(message)
		port.disconnect()
	})
}

//gcm_registration_start()
chrome.gcm.onMessage.addListener(gcm_message_received)

// chrome.runtime.onStartup.addListener function() {...};
// chrome.runtime.onInstalled.addListener function(object details) {...};