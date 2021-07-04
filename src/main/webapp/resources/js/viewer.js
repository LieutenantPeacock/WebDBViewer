const errormessage = document.querySelector('.error-message');
errormessage.querySelector('.close-icon').addEventListener('click', function() {
	errormessage.style.top = "-40px";
})
function showWarning(message) {
	errormessage.querySelector('.error-message-text').textContent = message;
	errormessage.style.top = "20px";
}
const databaseSelect = document.getElementById("database-select");
const tableSelect = document.getElementById("table-select");
const databaseOptions = databaseSelect
		.querySelectorAll('.custom-select-option');
for (let i = 0; i < databaseOptions.length; i++) {
	const current = databaseOptions[i];
	current.addEventListener('click', databaseOptionClickHandler);
}

function databaseOptionClickHandler() {
	const value = this.getAttribute('data-value');
	var preview = databaseSelect.querySelector('.custom-select-option.preview');
	preview.setAttribute('data-value', value);
	preview.innerHTML = this.innerHTML;
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			databaseDataHandler(this.response);
		}
	};
	xhttp.open("POST", "viewer?type=table&database=" + value, true);
	xhttp.send();
}

function databaseDataHandler(response) {
	const parsedJSON = JSON.parse(response);
	if (parsedJSON.success == true) {
		const tableNames = parsedJSON.data.tableNames;
		tableSelect.innerHTML = "<div class='custom-select-option preview'>Select a table</div>";
		tableSelect.parentElement.classList.remove('hidden');
		const optionContainers = document.createElement("div");
		optionContainers.classList.add('custom-select-options');
		for ( const index in tableNames) {
			const tableName = tableNames[index];
			const optionElement = document.createElement("div");
			optionElement.classList.add('custom-select-option');
			optionElement.textContent = tableName;
			optionElement.addEventListener('click', function() {
				tableSelect.querySelector('.custom-select-option.preview').textContent = tableName;
				const value = index;
				var xhttp = new XMLHttpRequest();
				xhttp.onreadystatechange = function() {
					if (this.readyState == 4 && this.status == 200) {
						tableDataHandler(this.response);
					}
				};
				xhttp.open("POST", "viewer?type=row&table="
						+ value
						+ "&database="
						+ databaseSelect.querySelector('.preview')
								.getAttribute('data-value'), true);
				xhttp.send();
			});
			optionContainers.appendChild(optionElement);
		}
		tableSelect.appendChild(optionContainers);
	} else {
		showWarning(parsedJSON.message);
	}
}
function tableDataHandler(response) {
	const parsedJSON = JSON.parse(response);
	if (parsedJSON.success === true) {
		const tableBody = document.querySelector('.table-view table tbody');
		tableBody.innerHTML = "<tr class='table-headers'></tr>";
		const data = parsedJSON.data;
		const tableHeaders = document.querySelector('.table-headers');
		for ( const columnName in data.columns) {
			var header = document.createElement("th");
			header.textContent = data.columns[columnName];
			tableHeaders.appendChild(header);
		}
		for ( const row in data.rows) {
			const realRow = data.rows[row];
			var rowElement = document.createElement("tr");
			for ( const column in data.columns) {
				var cell = document.createElement("td");
				const columnName = data.columns[column];
				cell.textContent = realRow[columnName];
				rowElement.appendChild(cell);
			}
			tableBody.appendChild(rowElement);
		}
	} else {
		showWarning(parsedJSON.message);
	}
}