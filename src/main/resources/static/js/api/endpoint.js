const endpoint = {
    car: {
        register: '/api/car/register',
        history: (data) => `/api/car/${data}/history`,
        resetPosition: '/api/car/reset/position',
        getParticipants: '/api/car/participants',
    },
    race: {
        enter: '/api/race/enter',
        start: '/api/race/start',
        leave: '/api/race/leave'
    },
    stomp: {
        connect: '/car-ws',
        subRaceLog: '/sub/race/log',
        subRaceResult: '/sub/race/result',
        subRaceParticipants: '/sub/race/participants'
    }
}
