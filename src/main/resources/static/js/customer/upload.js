const form = document.getElementById("uploadForm");
const pagesInput = document.getElementById("pages");
const copiesInput = document.getElementById("copies");
const printTypeInput = document.getElementById("printType");
const amountEl = document.getElementById("amount");
const messageEl = document.getElementById("message");

function calculateAmount() {
  const pages = Number(pagesInput.value || 1);
  const copies = Number(copiesInput.value || 1);
  const rate = printTypeInput.value === "Color" ? 10 : 2;
  const amount = pages * copies * rate;
  amountEl.textContent = `₹${amount}`;
  return amount;
}
copiesInput.addEventListener("input", calculateAmount);
printTypeInput.addEventListener("change", calculateAmount);
calculateAmount();

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const file = document.getElementById("file").files[0];
  if (!file) {
    messageEl.className = "mt-3 text-center text-danger";
    messageEl.textContent = "Please select a PDF.";
    return;
  }
  messageEl.className = "mt-3 text-center text-primary";
  messageEl.textContent = "Uploading...";
  const data = new FormData();
  data.append("file", file);
  try {
    const response = await fetch(UPLOAD_URL, { method: "POST", body: data });
    if (!response.ok) throw new Error(await response.text());
    const uploaded = await response.json();
    pagesInput.value = uploaded.pages;
    const amount = calculateAmount();
    sessionStorage.setItem("fileName", uploaded.fileName);
    sessionStorage.setItem("filePath", uploaded.filePath);
    sessionStorage.setItem("pages", uploaded.pages);
    sessionStorage.setItem("copies", copiesInput.value);
    sessionStorage.setItem("printType", printTypeInput.value);
    sessionStorage.setItem("amount", amount);
    window.location.href = "/customer/payment.html";
  } catch (error) {
    console.error(error);
    messageEl.className = "mt-3 text-center text-danger";
    messageEl.textContent = error.message || "Upload failed.";
  }
});
