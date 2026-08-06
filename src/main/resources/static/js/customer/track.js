async function checkStatus() {
  const id = sessionStorage.getItem("jobId");
  if (!id) {
    window.location.href = "/customer/upload.html";
    return;
  }
  try {
    const response = await fetch(`${BASE_URL}/track/${id}`);
    if (!response.ok) throw new Error();
    const job = await response.json();
    document.getElementById("queueNo").textContent = job.queueNumber;
    document.getElementById("fileName").textContent = job.fileName;
    const el = document.getElementById("status");
    el.textContent = job.status;
    el.className =
      job.status === "COMPLETED"
        ? "text-success"
        : job.status === "PRINTING"
        ? "text-primary"
        : "text-warning";
  } catch {
    document.getElementById("status").textContent = "Unable to load status";
  }
}
checkStatus();
setInterval(checkStatus, 3000);
