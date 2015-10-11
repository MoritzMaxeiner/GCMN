
function load()
{
	chrome.storage.local.get({
		sender_ids: '',
		native_port: 58372,
		receive_only: true,
		registration_id: ''
	}, function(items) {
		document.getElementById('sender-ids').value = items.sender_ids
		document.getElementById('native-port').value = items.native_port
		document.getElementById('receive-only').checked = items.receive_only
		document.getElementById('registration-id').value = items.registration_id
	})
}

function gcm_register_then_save(sender_ids)
{
	chrome.gcm.register(sender_ids.split(','), function(registration_id)
	{
		if(chrome.runtime.lastError)
		{
			console.error("Could not register with GCM")
			document.getElementById('registration-id').value = ''
		}
		else
		{
			//console.log(registration_id)
			document.getElementById('registration-id').value = registration_id
		}
		save()
	})
}

function ensure_gcm_registered_then_save()
{
	chrome.storage.local.get({
		sender_ids: '',
		registration_id: ''
	}, function(items) {
		var sender_ids = document.getElementById('sender-ids').value

		if(items.registration_id !== '' && items.sender_ids === sender_ids) save()
		else gcm_register_then_save(sender_ids)
	})
}

function save()
{
	var sender_ids = document.getElementById('sender-ids').value
	var native_port = document.getElementById('native-port').value
	var receive_only = document.getElementById('receive-only').checked
	var registration_id = document.getElementById('registration-id').value

	chrome.storage.local.set({
		sender_ids: sender_ids,
		native_port: native_port,
		receive_only: receive_only,
		registration_id: registration_id
	}, function() {
		var status = document.getElementById('status')
		status.textContent = 'Options saved.'
		setTimeout(function() {
			status.textContent = ''
		}, 1250)
	})
}

// Load and display GCM registration when the options page has fully loaded
document.addEventListener('DOMContentLoaded', load)

// Update and save GCM registration
document.getElementById('save').addEventListener('click', ensure_gcm_registered_then_save)