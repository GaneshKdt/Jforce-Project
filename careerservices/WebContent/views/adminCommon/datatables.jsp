
			$('#table').DataTable({
				dom: `
			    	<'row'<'col-sm-12 col-md-6'B><'col-sm-12 col-md-6'f>>
					<'row'<'col-sm-12'tr>>
					<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>`,
			    buttons: [
		             	{
			                extend: 'colvis',
			                text: 'Show/hide columns',
			                className:'m-1 btn-rounded'
		                },
		                {
		                    extend: 'copy',
		                    title: '${ tableTitle }',
		                    footer: true,
		                    exportOptions: {
			                     columns: ':visible'
		                 	},
			                className:'m-1 btn-rounded'
		                },
		                {
		                    extend: 'csv',
		                    title: '${ tableTitle }',
		                    footer: true,
		                    exportOptions: {
			                     columns: ':visible'
		                 	},
			                className:'m-1 btn-rounded'
		                },
		                {
		                    extend: 'excelHtml5',
		                    title: '${ tableTitle }',
		                    footer: true,
		                    exportOptions: {
			                     columns: ':visible'
		                 	},
			                className:'m-1 btn-rounded'
		                },
		                {
		                    extend: 'csv',
		                    title: '${ tableTitle }',
		                    footer: true,
		                    exportOptions: {
			                     columns: ':visible'
		                 	},
			                className:'m-1 btn-rounded'
		                },
		                {
		                    extend: 'pdfHtml5',
		                    title: '${ tableTitle }',
		                    footer: true,
			                orientation: 'landscape',
			                pageSize: 'A2',
		                    exportOptions: {
			                     columns: ':visible'
		                 	},
			                className:'m-1 btn-rounded'
		                },
		                {
		                    extend: 'print',
		                    title: '${ tableTitle }',
		                    exportOptions: {
			                     columns: ':visible'
		                 	},
			                className:'m-1 btn-rounded'
		                }
			    ]
			});