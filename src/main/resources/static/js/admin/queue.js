async function loadQueue() {
  const r = await fetch(BASE_URL);
  if (!r.ok) return;
  const all = await r.json();
  const jobs = all
    .filter(
      (j) =>
        String(j.paymentStatus).toUpperCase() === "PAID" &&
        ["PENDING", "PRINTING"].includes(j.status)
    )
    .sort((a, b) => a.queueNumber - b.queueNumber);
  document.getElementById("queueTable").innerHTML = jobs
    .map(
      (j) =>
        `<tr><td>${j.queueNumber}</td><td>${j.fileName}</td><td>${
          j.pages
        }</td><td>${j.copies}</td><td><span class="badge bg-${
          j.status === "PRINTING" ? "primary" : "warning text-dark"
        }">${j.status}</span></td></tr>`
    )
    .join("");
  document.getElementById("emptyQueue").style.display = jobs.length
    ? "none"
    : "block";
}
loadQueue();
setInterval(loadQueue, 3000);
