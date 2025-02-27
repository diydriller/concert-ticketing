import http from 'k6/http';

export const options = {
    scenarios: {
        getConcert: {
            executor: 'constant-arrival-rate',
            rate: 97,
            timeUnit: '1s',
            duration: '10m',
            preAllocatedVUs: 200,
            maxVUs: 500,
        }
    }
};

export default function () {
    const url = 'http://127.0.0.1:8080/concert';

    http.get(url);
}