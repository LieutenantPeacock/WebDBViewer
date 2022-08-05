;(function(){
	const csrfToken = document.querySelector("meta[name='_csrf']").content;
	const csrfHeader = document.querySelector("meta[name='_csrf_header']").content;
	const basePath = document.getElementById('basePath').value;
	document.querySelector('#connectionsContainer > .card.active')?.scrollIntoView(false);
	function _fetch(resource, options) {
		return fetch(resource, {...options, headers: {...options?.headers, [csrfHeader]: csrfToken}})
				.then(res => {
					if(!res.ok) throw Error('Response status: ' + res.status);
					return res.json();
				});
	}
	const connectionFormError = document.getElementById('connectionFormError');
	const newConnectionModal = document.getElementById('newConnectionModal');
	const noConnections = document.getElementById('noConnections');
	const connectionsContainer = document.getElementById('connectionsContainer');
	let processing = false;
	document.getElementById('newConnectionForm').addEventListener('submit', function(e){
		e.preventDefault();
		if (!processing) {
			processing = true;
			connectionFormError.textContent = '';
			_fetch(basePath + 'newConnection', {method: 'POST', body: new FormData(this)})
				.then(data => {
					if(!data.errors) {
						bootstrap.Modal.getInstance(newConnectionModal).hide();
						const connectionEl = connectionTemplate.content.cloneNode(true);
						const connection = data.value;
						connectionEl.querySelector('.card-title').textContent = connection.name;
						connectionEl.querySelector('details').innerHTML =
`<summary>Details</summary>
<strong class="text-decoration-underline">JDBC URL</strong>: ${connection.url} <br/>
<strong class="text-decoration-underline">Username</strong>: ${connection.username} <br/>
<strong class="text-decoration-underline">Driver Path</strong>: ${connection.driverPath} <br/>
<strong class="text-decoration-underline">Driver Class Name</strong>: ${connection.driverName}`;
						connectionEl.querySelector('a').href = basePath + `?connection=${data.value.id}`;
						if(noConnections) noConnections.style.display = 'none';
						connectionsContainer.appendChild(connectionEl);
					} else {
						showErrors(data.errors, 'connection_');
					}
				}).catch(error => {
					console.error(error);
					connectionFormError.textContent = 'An error occurred.';
				}).finally(()=> processing = false);
		}
	});
	const tableSelect = document.getElementById('tableSelect');
	if (tableSelect) {
		if (!tableSelect.querySelector('option[selected]'))
			tableSelect.selectedIndex = -1;
		tableSelect.addEventListener('change', function(e){
			const params = new URLSearchParams(location.search);
			params.set('table', this.value);
			location.search = params;
		});
	}
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
	function removeInvalid(elem) {
		elem.classList.remove('is-invalid');
	}
	function removeAllErrors(container) {
		container.querySelectorAll('.is-invalid').forEach(removeInvalid);
	}
	document.querySelectorAll('form.needs-validation select')
		.forEach(select => {
			select.addEventListener('change', function(e){
				removeInvalid(this);
			});
		});
	document.querySelectorAll('form.needs-validation input, form.needs-validation textarea')
		.forEach(input => {
			input.addEventListener('input', function(e){
				removeInvalid(this);
			});
		});
	function showErrors(errors, idPrefix = '') {
		for (const [inputName, error] of Object.entries(errors)) {
			const input = document.getElementById(idPrefix + inputName);
			if (input.nextElementSibling && input.nextElementSibling.matches('.invalid-feedback')) {
				const errorContainer = input.nextElementSibling;
				if (Array.isArray(error)) {
					if (error.length === 1) 
						errorContainer.textContent = error[0];
					else {
						errorContainer.textContent = '';
						const ul = document.createElement('ul');
						for (const e of error)
							ul.appendChild(Object.assign(document.createElement('li'), {textContent: e}));
						errorContainer.appendChild(ul);
					}
				} else {
					errorContainer.textContent = error;
				}
			}
			input.classList.add('is-invalid');
		}
	}
	document.getElementById('driverUploadBtn').addEventListener('click', function(e){
		removeAllErrors(driverUploadContainer);
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
				if (!data.errors) {
					driverPathSelect.add(new Option(data.value));
					driverPathSelect.value = data.value;
					onDriverPathChange();
					driverUploadContainer.querySelectorAll('input')
						.forEach(input => input.value = "");
				} else {
					showErrors(data.errors);
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
	const statementMessage = document.getElementById('statementMessage');
	const tableContents = document.getElementById('tableContents');
	document.getElementById('statementForm')?.addEventListener('submit', function(e){
		e.preventDefault();
		statementMessage.textContent = '';
		statementMessage.classList.remove('text-danger');
		const data = new FormData(this);
		data.append('connection', new URLSearchParams(location.search).get('connection'));
		_fetch(basePath + 'execute', {
			method: 'POST', body: data
		}).then(data => {
			if (data.errorMessage) {
				statementMessage.classList.add('text-danger');
				statementMessage.textContent = data.errorMessage;
			} else if(data.value.results){
				const tableData = data.value.results;
				tableContents.innerHTML = 
`<thead>
	<tr>
		${tableData.columns.map(column => 
			`<th scope="col" title="${column.name}: ${column.typeName}(${column.displaySize})">${column.name}</th>`)
			.join('')}
	</tr>
</thead>
<tbody>
	${tableData.rows.map(row => "<tr>" + 
		row.map(val => "<td>" + val + "</td>").join('')
		+ "</tr>").join('')}
</tbody>`;
			} else {
				statementMessage.textContent = 'Updated ' + data.value.updateCount + ' rows.';
			}
		}).catch(error => {
			statementMessage.classList.add('text-danger');
			statementMessage.textContent = 'An error occurred.';
		});
	});
	const userFormError = document.getElementById('userFormError');
	const usersList = document.querySelector('#usersContainer > ul');
	const newUserModal = document.getElementById('newUserModal');
	document.getElementById('newUserForm')?.addEventListener('submit', function(e){
		e.preventDefault();
		removeAllErrors(this);
		userFormError.textContent = '';
		_fetch(basePath + 'newUser', {body: new FormData(this), method: 'POST'})
			.then(data => {
				if (data.errors) {
					showErrors(data.errors, 'newUser_');
				} else {
					const li = document.createElement('li');
					li.className = 'list-group-item d-flex justify-content-between align-items-center';
					li.textContent = data.value.username;
					usersList.appendChild(li);
					bootstrap.Modal.getInstance(newUserModal).hide();
				}
			}).catch(error => {
				userFormError.textContent = 'An error occurred.';
			});
	});
})();