// ==========================================
// Track Print Job
// Uses BASE_URL from config.js
// ==========================================

let currentJobId = null;

// ==========================================
// Page Load
// ==========================================

document.addEventListener("DOMContentLoaded", () => {

    const savedJobId =
        sessionStorage.getItem("jobId");

    /*
     * If customer just completed payment,
     * automatically show that print job.
     */
    if (savedJobId) {

        currentJobId = savedJobId;

        document.getElementById(
            "jobIdInput"
        ).value = savedJobId;

        checkStatus();

    }

});


// ==========================================
// Track Job Entered Manually
// ==========================================

function trackEnteredJob() {

    const input =
        document.getElementById(
            "jobIdInput"
        );

    const message =
        document.getElementById(
            "message"
        );

    const id =
        Number(input.value);

    message.textContent = "";

    if (!id || id < 1) {

        message.textContent =
            "Please enter a valid Job ID.";

        return;
    }

    currentJobId = id;

    sessionStorage.setItem(
        "jobId",
        id
    );

    checkStatus();
}


// ==========================================
// Check Job Status
// ==========================================

async function checkStatus() {

    if (!currentJobId) {

        currentJobId =
            sessionStorage.getItem(
                "jobId"
            );

    }

    if (!currentJobId) {

        document.getElementById(
            "searchCard"
        ).style.display = "block";

        document.getElementById(
            "trackingCard"
        ).style.display = "none";

        return;
    }

    try {

        const response = await fetch(
            `${BASE_URL}/track/${currentJobId}`
        );

        if (!response.ok) {

            throw new Error(
                "Print job not found."
            );

        }

        const job =
            await response.json();

        // Show tracking result

        document.getElementById(
            "searchCard"
        ).style.display = "none";

        document.getElementById(
            "trackingCard"
        ).style.display = "block";


        // Job Details

        document.getElementById(
            "queueNo"
        ).textContent =
            job.queueNumber ?? "-";

        document.getElementById(
            "displayJobId"
        ).textContent =
            job.jobId ?? currentJobId;

        document.getElementById(
            "fileName"
        ).textContent =
            job.fileName ?? "-";

        document.getElementById(
            "paymentStatus"
        ).textContent =
            job.paymentStatus ?? "-";


        // Status

        const statusElement =
            document.getElementById(
                "status"
            );

        const status =
            (job.status || "PENDING")
                .toUpperCase();

        statusElement.textContent =
            status;

        if (status === "COMPLETED") {

            statusElement.className =
                "text-success";

        } else if (
            status === "PRINTING"
        ) {

            statusElement.className =
                "text-primary";

        } else if (
            status === "CANCELLED"
        ) {

            statusElement.className =
                "text-danger";

        } else {

            statusElement.className =
                "text-warning";

        }

    } catch (error) {

        console.error(
            "Tracking Error:",
            error
        );

        document.getElementById(
            "message"
        ).textContent =
            "Print job not found. Please check the Job ID.";

        document.getElementById(
            "searchCard"
        ).style.display = "block";

        document.getElementById(
            "trackingCard"
        ).style.display = "none";

        currentJobId = null;

        sessionStorage.removeItem(
            "jobId"
        );

    }

}


// ==========================================
// Track Another Job
// ==========================================

function trackAnotherJob() {

    currentJobId = null;

    sessionStorage.removeItem(
        "jobId"
    );

    document.getElementById(
        "jobIdInput"
    ).value = "";

    document.getElementById(
        "message"
    ).textContent = "";

    document.getElementById(
        "trackingCard"
    ).style.display = "none";

    document.getElementById(
        "searchCard"
    ).style.display = "block";

}


// ==========================================
// Auto Refresh
// ==========================================

setInterval(() => {

    if (currentJobId) {

        checkStatus();

    }

}, 3000);