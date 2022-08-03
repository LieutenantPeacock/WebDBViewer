;(function(){
	const csrfToken = document.querySelector("meta[name='_csrf']").content;
	const csrfHeader = document.querySelector("meta[name='_csrf_header']").content;
	const basePath = document.getElementById('basePath').value;
	function _fetch(resource, options) {
		return fetch(resource, {...options, headers: {...options?.headers, [csrfHeader]: csrfToken}})
				.then(res => {
					if(!res.ok) throw Error('Response status: ' + res.status);
					return res.json();
				});
	}
	const connectionFormError = document.getElementById('connectionFormError');
	const newConnectionModal = document.getElementById('newConnectionModal');
	let processing = false;
	document.getElementById('newConnectionForm').addEventListener('submit', function(e){
		e.preventDefault();
		if (!processing) {
			processing = true;
			this.classList.add('was-validated');
			connectionFormError.textContent = '';
			if (this.checkValidity()) {
				_fetch(basePath + 'newConnection', {method: 'POST', body: new FormData(this)})
					.then(data => {
						bootstrap.Modal.getInstance(newConnectionModal).hide();
						console.log(data);
					}).catch(error => {
						connectionFormError.textContent = 'An error occurred.';
					}).finally(()=>processing = false);
			}
		}
	});
	const driverUploadContainer = document.getElementById('driverUploadContainer');
	const driverUpload = document.getElementById('driverUpload');
	const driverUploadError = document.getElementById('driverUploadError');
	const driverFolderName = document.getElementById('driverFolderName');
	const driverFolderError = document.getElementById('driverFolderError');
	const driverPathSelect = document.getElementById('connection_driverPath');
	const driverNameContainer = document.getElementById('driverNameContainer');
	const driverPathError = document.getElementById('driverPathError');
	const driverNameSelect = document.getElementById('connection_driverName');
	driverPathSelect.selectedIndex = -1;
	driverUpload.addEventListener('change', function(e){
		this.classList.remove('is-invalid');
	});
	driverFolderName.addEventListener('input', function(e){
		this.classList.remove('is-invalid');
	})
	document.getElementById('driverUploadBtn').addEventListener('click', function(e){
		driverUploadContainer.querySelectorAll('.is-invalid')
				.forEach(el => el.classList.remove('is-invalid'));
		if (driverUpload.files.length) {
			this.disabled = true;
			const data = new FormData;
			for(const file of driverUpload.files) 
				data.append('files', file);
			data.append('folder', driverFolderName.value);
			_fetch(basePath + 'uploadDriver', {
				method: 'POST',
				body: data
			}).then(data => {
				for (const [inputId, error] of Object.entries(data.errors)) {
					const input = driverUploadContainer.querySelector('#' + inputId);
					input.nextElementSibling.textContent = error;
					input.classList.add('is-invalid');
				}
				if (data.success) {
					driverPathSelect.add(new Option(data.value));
					driverPathSelect.value = data.value;
					onDriverPathChange();
					driverUploadContainer.querySelectorAll('input')
						.forEach(input => input.value = "");
				}
			}).catch(error => {
				driverUploadError.textContent = 'An error occurred.';
				driverUpload.classList.add('is-invalid');
			}).finally(() => {
				this.disabled = false;
			});
		}
	});
	function onDriverPathChange() {
		driverPathSelect.classList.remove('is-invalid');
		driverNameContainer.style.display = 'none';
		_fetch(basePath + 'getDrivers?' + new URLSearchParams({driverPath: driverPathSelect.value}))
		  .then(data => {
			for(const driver of data) {
				const option = document.createElement('option');
				option.text = driver.name + (driver.deprecated ? ' (DEPRECATED)': '');
				option.value = driver.name;
				if (driver.deprecated) option.style.backgroundColor = '#808080';
				driverNameSelect.add(option);
			}
			driverNameContainer.style.display = '';
		}).catch(error => {
			driverPathError.textContent = 'An error occurred fetching the drivers at the specified location.';
			driverPathSelect.classList.add('is-invalid');
		});
	}
	driverPathSelect.addEventListener('change', onDriverPathChange);
})();