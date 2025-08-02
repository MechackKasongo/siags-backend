/** @odoo-module **/

import {ListRenderer} from '@web/views/list/list_renderer';

ListRenderer.include({
    setup() {
        this._super(...arguments);
    },

    _renderHeader() {
        const thead = this._super(...arguments);

        if (this.props.list.modelName === 'medical.bill') {
            console.log("✅ Coloring headers for 'medical.bill'");

            // Apply custom header background colors
            thead.querySelectorAll('th').forEach(th => {
                const name = th.getAttribute('data-name');
                if (name === 'patient_id') {
                    th.style.backgroundColor = '#d0ebff';  // Light blue
                } else if (name === 'patient_name') {
                    th.style.backgroundColor = '#d3f9d8';  // Light green
                } else if (name === 'patient_age') {
                    th.style.backgroundColor = '#dee2e6';  // Light gray
                } else if (name === 'patient_gender') {
                    th.style.backgroundColor = '#fff3bf';  // Light yellow
                } else if (name === 'total_amount') {
                    th.style.backgroundColor = '#e5dbff';  // Light purple
                }
            });
        }

        return thead;
    },
});
