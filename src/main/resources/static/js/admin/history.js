let historyJobs = [];
async function loadHistory() {
  const r = await fetch(`${BASE_URL}/history`);
  if (!r.ok) return;
  historyJobs = await r.json();
  display(historyJobs);
}
function display(jobs) {
  document.getElementById("historyTable").innerHTML = jobs
    .map(
      (j) =>
        `<tr><td>${j.queueNumber}</td><td>${j.fileName}</td><td>${j.pages}</td><td>${j.copies}</td><td>${j.printType}</td><td>₹${j.amount}</td><td>${j.paymentStatus}</td><td><span class="badge bg-success">COMPLETED</span></td></tr>`
    )
    .join("");
}
document
  .getElementById("search")
  .addEventListener("input", (e) =>
    display(
      historyJobs.filter((j) =>
        j.fileName.toLowerCase().includes(e.target.value.toLowerCase())
      )
    )
  );
loadHistory();
setInterval(loadHistory, 5000);
