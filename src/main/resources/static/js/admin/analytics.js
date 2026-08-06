let sChart, pChart;
async function load() {
  const r = await fetch(BASE_URL);
  if (!r.ok) return;
  const jobs = await r.json();
  const pending = jobs.filter((j) => j.status === "PENDING").length,
    printing = jobs.filter((j) => j.status === "PRINTING").length,
    completed = jobs.filter((j) => j.status === "COMPLETED").length,
    paid = jobs.filter((j) => String(j.paymentStatus).toUpperCase() === "PAID"),
    revenue = paid.reduce((s, j) => s + Number(j.amount || 0), 0),
    bw = jobs.filter((j) => j.printType === "Black & White").length,
    color = jobs.filter((j) => j.printType === "Color").length;
  totalJobs.textContent = jobs.length;
  pendingJobs.textContent = pending;
  completedJobs.textContent = completed;
  document.getElementById("revenue").textContent = `₹${revenue.toFixed(2)}`;
  if (sChart) sChart.destroy();
  sChart = new Chart(statusChart, {
    type: "pie",
    data: {
      labels: ["Pending", "Printing", "Completed"],
      datasets: [{ data: [pending, printing, completed] }],
    },
  });
  if (pChart) pChart.destroy();
  pChart = new Chart(printChart, {
    type: "doughnut",
    data: {
      labels: ["Black & White", "Color"],
      datasets: [{ data: [bw, color] }],
    },
  });
}
load();
setInterval(load, 5000);
