<template>

    <v-data-table
        :headers="headers"
        :items="selectSeat"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'SelectSeatView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            selectSeat : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/selectSeats'))

            temp.data._embedded.selectSeats.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.selectSeat = temp.data._embedded.selectSeats;
        },
        methods: {
        }
    }
</script>

