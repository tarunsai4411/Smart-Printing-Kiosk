let jobs = [],
  filteredJobs = [];
async function loadJobs() {
  try {
    const r = await fetch(BASE_URL);
    if (!r.ok) throw new Error();
    jobs = await r.json();
    filteredJobs = [...jobs];
    displayJobs(filteredJobs);
  } catch {
    document.getElementById("loading").innerHTML =
      '<p class="text-danger">Unable to load jobs.</p>';
  }
}
function displayJobs(list) {
  document.getElementById("jobTable").innerHTML = list
    .map(
      (j) =>
        `<tr><td>${j.queueNumber}</td><td>${j.jobId}</td><td>${escapeHtml(
          j.fileName
        )}</td><td>${j.pages}</td><td>${j.copies}</td><td>${
          j.printType
        }</td><td>${badge(j.status)}</td><td>${paymentBadge(
          j.paymentStatus
        )}</td><td>₹${Number(j.amount).toFixed(2)}</td><td>${actions(
          j
        )}</td></tr>`
    )
    .join("");
  document.getElementById("loading").style.display = "none";
  document.getElementById("tableContainer").style.display = "block";
  document.getElementById("noData").style.display = list.length
    ? "none"
    : "block";
}
function actions(j) {
  if (j.status === "COMPLETED")
    return '<span class="badge bg-success">Printed</span>';
  if (j.status === "CANCELLED")
    return '<span class="badge bg-danger">Cancelled</span>';
  const view = j.filePath
    ? `<button class="btn btn-info btn-sm me-1" onclick="viewFile(${j.jobId})">View</button>`
    : "";
  const edit =
    j.status === "PENDING"
      ? `<a class="btn btn-warning btn-sm me-1" href="/admin/edit-job.html?id=${j.jobId}">Edit</a>`
      : "";
  const cancel =
    j.status === "PENDING"
      ? `<button class="btn btn-danger btn-sm me-1" onclick="cancelJob(${j.jobId})">Cancel</button>`
      : "";
  const del = `<button class="btn btn-outline-danger btn-sm" onclick="deleteJob(${j.jobId})">Delete</button>`;
  return view + edit + cancel + del;
}
function badge(s) {
  const c =
    s === "COMPLETED"
      ? "success"
      : s === "PRINTING"
      ? "primary"
      : s === "CANCELLED"
      ? "danger"
      : "warning text-dark";
  return `<span class="badge bg-${c}">${s}</span>`;
}
function paymentBadge(p) {
  return `<span class="badge bg-${
    String(p).toUpperCase() === "PAID" ? "success" : "warning text-dark"
  }">${p}</span>`;
}
function viewFile(id) {
  window.open(`${BASE_URL}/view/${id}`, "_blank");
}
async function deleteJob(id) {
  if (!confirm("Delete this job?")) return;
  const r = await fetch(`${BASE_URL}/${id}`, { method: "DELETE" });
  if (!r.ok) {
    showToast("Unable to delete", false);
    return;
  }
  showToast("Job deleted");
  loadJobs();
}
async function cancelJob(id) {
  if (!confirm("Cancel this job?")) return;
  const r = await fetch(`${BASE_URL}/cancel/${id}`, { method: "PUT" });
  if (!r.ok) {
    showToast((await r.json()).message || "Unable to cancel", false);
    return;
  }
  showToast("Job cancelled");
  loadJobs();
}
document.getElementById("searchInput").addEventListener("input", (e) => {
  const k = e.target.value.toLowerCase();
  filteredJobs = jobs.filter((j) =>
    [j.fileName, j.status, j.paymentStatus, j.queueNumber].some((v) =>
      String(v).toLowerCase().includes(k)
    )
  );
  displayJobs(filteredJobs);
});
document.getElementById("sortBy").addEventListener("change", (e) => {
  const v = e.target.value;
  if (v === "queueAsc")
    filteredJobs.sort((a, b) => a.queueNumber - b.queueNumber);
  if (v === "queueDesc")
    filteredJobs.sort((a, b) => b.queueNumber - a.queueNumber);
  if (v === "amountAsc") filteredJobs.sort((a, b) => a.amount - b.amount);
  if (v === "amountDesc") filteredJobs.sort((a, b) => b.amount - a.amount);
  displayJobs(filteredJobs);
});
function exportCSV() {
  const rows = [
    [
      "Queue",
      "ID",
      "File",
      "Pages",
      "Copies",
      "Type",
      "Status",
      "Payment",
      "Amount",
    ],
    ...filteredJobs.map((j) => [
      j.queueNumber,
      j.jobId,
      j.fileName,
      j.pages,
      j.copies,
      j.printType,
      j.status,
      j.paymentStatus,
      j.amount,
    ]),
  ];
  const csv = rows
    .map((r) =>
      r.map((v) => `"${String(v ?? "").replaceAll('"', '""')}"`).join(",")
    )
    .join("\n");
  const a = document.createElement("a");
  a.href = URL.createObjectURL(new Blob([csv], { type: "text/csv" }));
  a.download = "print-jobs.csv";
  a.click();
}
function showToast(m, ok = true) {
  const el = document.getElementById("liveToast");
  el.className = `toast ${ok ? "text-bg-success" : "text-bg-danger"}`;
  document.getElementById("toastMessage").textContent = m;
  bootstrap.Toast.getOrCreateInstance(el).show();
}
function escapeHtml(v) {
  return String(v ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
loadJobs();
