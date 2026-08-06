async function loadDisplay() {
  try {
    const r = await fetch(`${BASE_URL}/current`);
    if (r.status === 204) {
      queue.textContent = "--";
      file.textContent = "No Document Printing";
      status.textContent = "Waiting for Print Jobs";
      return;
    }
    if (!r.ok) throw new Error();
    const j = await r.json();
    queue.textContent = j.queueNumber;
    file.textContent = j.fileName;
    status.textContent = "Printing in Progress";
  } catch {
    queue.textContent = "--";
    file.textContent = "Server Offline";
    status.textContent = "Unable to connect";
  }
}
loadDisplay();
setInterval(loadDisplay, 3000);
