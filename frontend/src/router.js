
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import MusicalItemManager from "./components/listers/MusicalItemCards"
import MusicalItemDetail from "./components/listers/MusicalItemDetail"
import MusicalSeatManager from "./components/listers/MusicalSeatCards"
import MusicalSeatDetail from "./components/listers/MusicalSeatDetail"

import ReservationReservationManager from "./components/listers/ReservationReservationCards"
import ReservationReservationDetail from "./components/listers/ReservationReservationDetail"




export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/musicals/items',
                name: 'MusicalItemManager',
                component: MusicalItemManager
            },
            {
                path: '/musicals/items/:id',
                name: 'MusicalItemDetail',
                component: MusicalItemDetail
            },
            {
                path: '/musicals/seats',
                name: 'MusicalSeatManager',
                component: MusicalSeatManager
            },
            {
                path: '/musicals/seats/:id',
                name: 'MusicalSeatDetail',
                component: MusicalSeatDetail
            },

            {
                path: '/reservations/reservations',
                name: 'ReservationReservationManager',
                component: ReservationReservationManager
            },
            {
                path: '/reservations/reservations/:id',
                name: 'ReservationReservationDetail',
                component: ReservationReservationDetail
            },





    ]
})
