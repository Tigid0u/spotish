# Spotish

## Get started

First, clone the repository:

```bash
git clone git@github.com:Tigid0u/spotish.git
```

### Backend

First, cd into the backend directory:

```bash
cd /backend
```

Then, build the project using Maven:

```bash
mvn clean compile
```

Then run the javalin application:

```bash
java -jar target/spotish.jar
```

You should see something like this in the console:

```plaintext
Javalin started in ... ms
Listening on http://localhost:7070
```

### Frontend

First, cd into the frontend directory:

```bash
cd ../frontend
```

Then, install the dependencies using npm:

```bash
npm install
```

Then, run the vue development server:

```bash
npm run dev
```

It usually runs on <http://localhost:5173> but check the console to be sure.
