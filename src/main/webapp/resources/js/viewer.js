;(function(){
	const csrfToken = document.querySelector("meta[name='_csrf']").content;
	const csrfHeader = document.querySelector("meta[name='_csrf_header']").content;
	const basePath = document.getElementById('basePath').value;
	const userId = $('#userId').val();
	document.querySelector('#connectionsContainer > .card.active')?.scrollIntoView(false);
	function _fetch(resource, options) {
		return fetch(resource, {...options, headers: {...options?.headers, [csrfHeader]: csrfToken}})
				.then(res => {
					if(!res.ok) throw Error('Response status: ' + res.status);
					return res.json();
				});
	}
	const connectionFormError = document.getElementById('connectionFormError');
	const connectionModal = document.getElementById('connectionModal');
	const noConnections = document.getElementById('noConnections');
	const connectionsContainer = document.getElementById('connectionsContainer');
	const $connectionForm = $('#connectionForm');
	connectionModal.addEventListener('hide.bs.modal', function(e){
		$(this).find('.modal-footer > .alert').remove();
	});
	$('#newConnectionBtn').click(function(e){
		$connectionForm[0].reset();
		$('#connectionModalLabel').text('New Connection');
		$('#connection_driverPath').prop('selectedIndex', -1);
		$connectionForm.find('input[name=id]').val('');
		$('#driverNameContainer').hide();
		$('#connectionUsers').hide();
	});
	function setConnectionData(connectionEl, connection) {
		const container = connectionEl.querySelector('.connection-container');
		if(container) container.dataset.id = connection.id;
		connectionEl.querySelector('.connection-name').textContent = connection.name;
		connectionEl.querySelector('details').innerHTML =
`<summary>Details</summary>
<strong class="text-decoration-underline">JDBC URL</strong>: ${connection.url} <br/>
<strong class="text-decoration-underline">Username</strong>: ${connection.username} <br/>
<strong class="text-decoration-underline">Driver Path</strong>: ${connection.driverPath} <br/>
<strong class="text-decoration-underline">Driver Class Name</strong>: ${connection.driverName}`;
		connectionEl.querySelector('a').href = basePath + `?connection=${connection.id}`;
	}
	let processing = false;
	$connectionForm.submit(function(e){
		e.preventDefault();
		if (!processing) {
			processing = true;
			connectionFormError.textContent = '';
			if (!$connectionForm.find('input[name=id]').val()) {
				_fetch(basePath + 'newConnection', {method: 'POST', body: new FormData(this)})
					.then(data => {
						if(!data.errors) {
							bootstrap.Modal.getInstance(connectionModal).hide();
							const connectionEl = connectionTemplate.content.cloneNode(true);
							setConnectionData(connectionEl, data.value);
							if(noConnections) noConnections.style.display = 'none';
							connectionsContainer.appendChild(connectionEl);
						} else {
							showErrors(data.errors, 'connection_');
						}
					}).catch(error => {
						console.error(error);
						connectionFormError.textContent = 'An error occurred.';
					}).finally(()=> processing = false);
			} else {
				const formData = new FormData(this);
				$('#connectionUsers > ul > li').each(function(){
					formData.append('userIds', $(this).data('id'));
				});
				_fetch(basePath + 'updateConnection', {method: 'POST', body: formData})
					.then(data => {
						if(data.errors) {
							showErrors(data.errors, 'connection_');
						} else {
							bootstrap.Modal.getInstance(connectionModal).hide();
							setConnectionData(
								document.querySelector(`.connection-container[data-id="${data.value.id}"]`),
								data.value
							);
						}
					})
					.catch(error => {
						$('#connectionAddUserError').text('An error occurred.');
					}).finally(() => processing = false);
			}
		}
	});
	const $connection_url = $('#connection_url');
	const jdbcUrlFormats = new Set([...document.querySelectorAll('#connection_type > option')]
		.map(el => el.dataset.urlFormat).filter(Boolean));
	$('#connection_type').change(function(e){
		if (!$connection_url.val().trim() || jdbcUrlFormats.has($connection_url.val()))
			$connection_url.val($('option:checked', this).data('url-format'));
	});
	$('#testConnectionBtn').click(function(e){
		const showAlert = (type, message)=>{
			$(this).parent().find('.alert').remove();
			$(this).parent().append(
`<div class="alert alert-dismissible alert-${type} w-100" role="alert">
	<div>${message}</div>
	<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>`
			);
		};
		_fetch(basePath + 'testConnection', {method: 'POST', body: new FormData($connectionForm[0])})
			.then(data => {
				showAlert(data.errorMessage ? 'danger' : 'success', data.value || data.errorMessage);
			}).catch(err => {
				showAlert('danger', 'An error occurred while attempting to test the connection.');
			});
	});
	const connectionUserIds = new Set;
	$('#connectionsContainer').on('click', '.connection-edit', function(e){
		_fetch(basePath + 'connectionDetails?id=' + $(this).parents('.connection-container').data('id'))
			.then(data => {
				$('#connectionModalLabel').text('Edit Connection');
				for (const [name, value] of Object.entries(data)) {
					$connectionForm.find(`[name=${name}]`).val(value);
				}
				$('#connectionUsers').show();
				$('#noConnectionUsers').toggle(!data.users.length);
				$('#connectionUsers > ul').empty();
				$('#connectionAddUser').val('');
				connectionUserIds.clear();
				data.users.forEach(addConnectionUser);
				onDriverPathChange().then(() => {
					$connectionForm.find('input[name=driverName]').val(data.driverName);
					new bootstrap.Modal(connectionModal).show();
				});
			});
	});
	function addConnectionUser(user) {
		$('#connectionUsers > ul').append(
			`<li class="list-group-item d-flex justify-content-between align-items-center"
				data-id="${user.id}">
				<span>${user.username} ${user.id == userId ? 
					'<span class="text-info fw-bold fst-italic">(You)</span>': ''}
				</span>
				${user.id != userId ? 
				'<span class="material-icons remove-connection-user" title="Remove User">cancel</span>' : ''}
			</li>`);
		connectionUserIds.add(user.id);
	}
	$('#connectionUsers').on('click', '.remove-connection-user', function(e){
		const li = $(this).parent();
		connectionUserIds.delete(li.data('id'));
		li.remove();
	});
	$('#connectionAddUser').autocomplete({
		appendTo: '#connectionModal',
		position: {collision: "flip"},
		source: function(request, response) {
			_fetch(basePath + 'autocompleteUsersNotInConnection?' + new URLSearchParams({
				connectionId: $connectionForm.find('input[name=id]').val(), text: request.term
			})).then(users => {
				response(users.filter(({id})=>!connectionUserIds.has(id)).map(({username}) => username));
			}).catch(e => response([]));
		}
	}).on('input', function(e){
		$('#connectionAddUserError').text('');
	});
	$('#connectionAddUserForm').submit(function(e){
		e.preventDefault();
		$('#connectionAddUser').autocomplete('close');
		_fetch(basePath + 'userinfo?username=' + $('#connectionAddUser').val())
			.then(data => {
				if (data.errorMessage)
					$('#connectionAddUserError').text(data.errorMessage)
				else if(connectionUserIds.has(data.value.id))
					$('#connectionAddUserError').text('User already has access to this connection.');
				else {
					addConnectionUser(data.value);
					this.reset();
				}
			})
			.catch(error => {
				console.error(error);
				$('#connectionAddUserError').text('An error occurred.');
			});
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
	document.getElementById('driverUploadBtn')?.addEventListener('click', function(e){
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
		return _fetch(basePath + 'getDrivers?' + new URLSearchParams({driverPath: driverPathSelect.value}))
		  .then(data => {
			while (driverNameSelect.options.length) driverNameSelect.options.remove(0);
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
	driverPathSelect?.addEventListener('change', onDriverPathChange);
	const statementMessage = document.getElementById('statementMessage');
	const $queryResultContents = $('#queryResultContents');
	function getConnectionId() {
		return new URLSearchParams(location.search).get('connection')
	}
	document.getElementById('statementForm')?.addEventListener('submit', function(e){
		e.preventDefault();
		statementMessage.textContent = '';
		statementMessage.classList.remove('text-danger');
		const data = new FormData(this);
		data.append('connection', getConnectionId());
		_fetch(basePath + 'execute', {
			method: 'POST', body: data
		}).then(data => {
			if (data.errorMessage) {
				statementMessage.classList.add('text-danger');
				statementMessage.textContent = data.errorMessage;
			} else {
				if(data.value.results){
					const tableData = data.value.results;
					$queryResultContents.empty();
					const $thead = $('<thead/>');
					const $theadrow = $('<tr/>');
					for (const column of tableData.columns) {
						$theadrow.append(`<th scope="col"><span title="${column.name}: ${column.typeName}(${column.displaySize})">${column.name}</span></th>`);
					}
					$thead.append($theadrow);
					$queryResultContents.append($thead);
					const $tbody = $('<tbody/>');
					for (const row of tableData.rows) {
						const $tr = $('<tr/>');
						for (const val of row) {
							$tr.append("<td>" + val + "</td>");
						}
						$tbody.append($tr);
					}
					$queryResultContents.append($tbody);
					statementMessage.textContent = data.value.results.message;
				} else {
					statementMessage.textContent = 'Updated ' + data.value.updateCount + ' rows.';
				}
			}
		}).catch(error => {
			statementMessage.classList.add('text-danger');
			statementMessage.textContent = 'An error occurred.';
		});
	});
	const sortDir = ["NONE", "ASC", "DESC"];
	$('#tableContents').on('click', '.sort-icon', function(e){
		const sortIdx = (+$(this).data('sort-idx') + 1) % sortDir.length;
		const params = new URLSearchParams(location.search);
		params.set('sort', this.parentElement.querySelector('span').textContent);
		if (sortIdx === 0)
			params.delete('sort'), params.delete('dir');
		else 
			params.set('dir', sortDir[sortIdx]);
		location.search = params;
	});
	$('#whereFilterForm').submit(function(e){
		e.preventDefault();
		const params = new URLSearchParams(location.search);
		params.set('where', $('input', this).val());
		location.search = params;
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
					this.reset();
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