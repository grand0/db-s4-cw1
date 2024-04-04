Chart.defaults.color = "#f1f1f1";

const summaryMonthPicker = $("#summary-month")

let selectedStatsBy = "countries"
let selectedStatsFor = "amount"

const summaryChart = new Chart($('#summary-chart'), {
    type: 'bar',
    data: {
        labels: [],
        datasets: [
            {
                label: 'Total amount ($)',
                data: [],
                borderWidth: 2,
                yAxisID: 'amountAxis'
            },
            {
                label: 'Total transactions',
                data: [],
                borderWidth: 2,
                yAxisID: 'transactionsAxis'
            },
        ]
    },
    options: {
        scales: {
            amountAxis: {
                title: {
                    display: true,
                    text: 'Total amount ($)'
                },
                position: 'left',
            },
            transactionsAxis: {
                title: {
                    display: true,
                    text: 'Total transactions'
                },
                position: 'right',
            }
        },
        layout: {
            padding: 20
        },
        plugins: {
            title: {
                text: "Top-10 countries in total amount",
                display: true,
                font: {
                    size: 24
                }
            },
            colors: {
                forceOverride: true,
            },
        }
    }
});

const monthSummaryChart = new Chart($('#month-summary-chart'), {
    type: 'bar',
    data: {
        labels: [],
        datasets: [
            {
                label: 'Total amount ($)',
                data: [],
                borderWidth: 2,
                yAxisID: 'amountAxis'
            },
            {
                label: 'Total transactions',
                data: [],
                borderWidth: 2,
                yAxisID: 'transactionsAxis'
            },
        ]
    },
    options: {
        scales: {
            amountAxis: {
                title: {
                    display: true,
                    text: 'Total amount ($)'
                },
                position: 'left',
            },
            transactionsAxis: {
                title: {
                    display: true,
                    text: 'Total transactions'
                },
                position: 'right',
            }
        },
        layout: {
            padding: 20
        },
        plugins: {
            title: {
                text: "Top-10 countries in total amount",
                display: true,
                font: {
                    size: 24
                }
            },
            colors: {
                forceOverride: true,
            },
        }
    }
})

function updateSummaryChart(summary) {
    summaryChart.data.labels = []
    summaryChart.data.datasets[0].data = []
    summaryChart.data.datasets[1].data = []

    const statsByText = textForParam(selectedStatsBy)
    const statsForText = textForParam(selectedStatsFor)
    summaryChart.options.plugins.title.text = "Top-10 " + statsByText + " in " + statsForText + " (all time)"

    let sortFunc = (e1, e2) => {}
    switch (selectedStatsFor) {
        case "amount":
            sortFunc = (e1, e2) => e2[1]['totalAmount'] - e1[1]['totalAmount']
            break;
        case "count":
            sortFunc = (e1, e2) => e2[1]['transactionCount'] - e1[1]['transactionCount']
            break;
    }

    const top10 = Object.entries(summary)
        .sort(sortFunc)
        .slice(0, 10)
    for (const [key, value] of top10) {
        summaryChart.data.labels.push(key)
        summaryChart.data.datasets[0].data.push(value['totalAmount'])
        summaryChart.data.datasets[1].data.push(value['transactionCount'])
    }
    summaryChart.update()
}

function updateMonthSummaryChart(month, monthSummary) {
    monthSummaryChart.data.labels = []
    monthSummaryChart.data.datasets[0].data = []
    monthSummaryChart.data.datasets[1].data = []

    const statsByText = textForParam(selectedStatsBy)
    const statsForText = textForParam(selectedStatsFor)
    monthSummaryChart.options.plugins.title.text = "Top-10 " + statsByText + " in " + statsForText + " (on " + month + ")"

    let sortFunc = (e1, e2) => {}
    switch (selectedStatsFor) {
        case "amount":
            sortFunc = (e1, e2) => e2[1]['totalAmount'] - e1[1]['totalAmount']
            break;
        case "count":
            sortFunc = (e1, e2) => e2[1]['transactionCount'] - e1[1]['transactionCount']
            break;
    }

    const top10 = Object.entries(monthSummary)
        .sort(sortFunc)
        .slice(0, 10)
    for (const [key, value] of top10) {
        monthSummaryChart.data.labels.push(key)
        monthSummaryChart.data.datasets[0].data.push(value['totalAmount'])
        monthSummaryChart.data.datasets[1].data.push(value['transactionCount'])
    }
    monthSummaryChart.update()
}

function tabByIdToParam(id) {
    switch (id) {
        case "tab-by-countries":
            return "countries"
        case "tab-by-currencies":
            return "currencies"
        case "tab-by-payment-statuses":
            return "payment_statuses"
    }
    return "countries"
}
function tabForIdToParam(id) {
    switch (id) {
        case "tab-for-amount":
            return "amount"
        case "tab-for-count":
            return "count"
    }
    return "amount"
}
function textForParam(param) {
    switch (param) {
        case "amount":
            param = "total amount"
            break;
        case "count":
            param = "transactions count"
            break;
        case "payment_statuses":
            param = "payment statuses"
            break;
    }
    return param
}

function fetchSummaryAndUpdate() {
    $.get("/stats.json", {"by": selectedStatsBy}, function (data) {
        updateSummaryChart(data)
    }, "json")
}
function fetchMonthSummaryAndUpdate() {
    const input = summaryMonthPicker.val()
    const dateEntered = new Date(input)
    const dateStr = dateEntered.getFullYear() + "-" + (dateEntered.getMonth() + 1) + "-" + dateEntered.getDate()
    $.get("/stats.json/month", {"by": selectedStatsBy, "date": dateStr}, function (data) {
        updateMonthSummaryChart(data['month'], data['monthSummary'])
    })
}

$.get("/stats.json", function (data) {
    updateSummaryChart(data)
}, "json")

summaryMonthPicker.on("change", fetchMonthSummaryAndUpdate)
summaryMonthPicker[0].valueAsDate = new Date()

fetchSummaryAndUpdate()
fetchMonthSummaryAndUpdate()

document.querySelectorAll('#stats-by-tabs a').forEach(triggerEl => {
    const tabTrigger = new bootstrap.Tab(triggerEl)
    triggerEl.addEventListener('click', event => {
        event.preventDefault()
        tabTrigger.show()

        selectedStatsBy = tabByIdToParam(triggerEl.id)
        fetchSummaryAndUpdate()
        fetchMonthSummaryAndUpdate()
    })
})

document.querySelectorAll('#stats-for-tabs a').forEach(triggerEl => {
    const tabTrigger = new bootstrap.Tab(triggerEl)
    triggerEl.addEventListener('click', event => {
        event.preventDefault()
        tabTrigger.show()

        selectedStatsFor = tabForIdToParam(triggerEl.id)
        fetchSummaryAndUpdate()
        fetchMonthSummaryAndUpdate()
    })
})
