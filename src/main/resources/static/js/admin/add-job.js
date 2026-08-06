let uploaded = null;
const pages = document.getElementById("pages"),
  copies = document.getElementById("copies"),
  type = document.getElementById("printType");
function amount() {
  const v =
    Number(pages.value || 0) *
    Number(copies.value || 0) *
    (type.value === "Color" ? 10 : 2);
  document.getElementById("amountDisplay").value = `₹${v}`;
  return v;
}
copies.addEventListener("input", amount);
type.addEventListener("change", amount);
document.getElementById("file").addEventListener("change", async (e) => {
  const file = e.target.files[0];
  if (!file) return;
  const data = new FormData();
  data.append("file", file);
  const r = await fetch(UPLOAD_URL, { method: "POST", body: data });
  if (!r.ok) {
    document.getElementById("uploadStatus").textContent = await r.text();
    return;
  }
  uploaded = await r.json();
  pages.value = uploaded.pages;
  document.getElementById("uploadStatus").className = "mb-3 text-success";
  document.getElementById("uploadStatus").textContent = "Uploaded successfully";
  amount();
});
document.getElementById("jobForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  if (!uploaded) {
    alert("Upload a PDF first.");
    return;
  }
  const job = {
    fileName: uploaded.fileName,
    filePath: uploaded.filePath,
    pages: Number(pages.value),
    copies: Number(copies.value),
    printType: type.value,
    amount: amount(),
    status: "PENDING",
    paymentStatus: document.getElementById("paymentStatus").value,
  };
  const r = await fetch(BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(job),
  });
  if (!r.ok) {
    alert((await r.json()).message || "Unable to create job");
    return;
  }
  location.href = "/admin/jobs.html";
});
