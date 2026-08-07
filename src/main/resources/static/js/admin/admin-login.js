document.addEventListener("DOMContentLoaded", () => {

  const form =
      document.getElementById("loginForm");

  const message =
      document.getElementById("message");

  const params =
      new URLSearchParams(
          window.location.search
      );

  if (params.get("error") === "true") {

      message.className =
          "text-danger text-center mb-3";

      message.textContent =
          "Invalid username or password.";

  }

  if (params.get("logout") === "true") {

      message.className =
          "text-success text-center mb-3";

      message.textContent =
          "Logged out successfully.";

  }

  form.addEventListener(
      "submit",
      async function (event) {

          event.preventDefault();

          const username =
              document
                  .getElementById("username")
                  .value
                  .trim();

          const password =
              document
                  .getElementById("password")
                  .value;

          message.textContent = "";

          if (!username || !password) {

              message.className =
                  "text-danger text-center mb-3";

              message.textContent =
                  "Please enter username and password.";

              return;
          }

          const formData =
              new URLSearchParams();

          formData.append(
              "username",
              username
          );

          formData.append(
              "password",
              password
          );

          try {

              const response =
                  await fetch(
                      "/login",
                      {
                          method: "POST",

                          headers: {
                              "Content-Type":
                                  "application/x-www-form-urlencoded"
                          },

                          body:
                              formData.toString()
                      }
                  );

              if (response.redirected) {

                  window.location.href =
                      response.url;

                  return;
              }

              if (response.ok) {

                  window.location.href =
                      "/admin/dashboard.html";

                  return;
              }

              message.className =
                  "text-danger text-center mb-3";

              message.textContent =
                  "Invalid username or password.";

          } catch (error) {

              console.error(
                  "Login Error:",
                  error
              );

              message.className =
                  "text-danger text-center mb-3";

              message.textContent =
                  "Unable to connect to server.";

          }

      }
  );

});