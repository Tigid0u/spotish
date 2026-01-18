# 🎶 Spotish

Spotish is a music web application that allows users to search for songs, artists and create playlists. It is built using Java with Javalin for the backend and Vue.js for the frontend. The backend serves an API that the frontend consumes to provide a seamless user experience.

This project is a practical work done in the context of the course "Développement d'applications web (DAI)" at HEIG-VD.

## Authors

Have contributed to this project:

- Alberto De Sousa Lopes [@Alb-E](https://github.com/Alb-E)
- Maikol Correia Da Silva [@Maikol-Da-Silva](https://github.com/Maikol-Da-Silva)
- Nolan Evard [@Tigid0u](https://github.com/Tigid0u)

## Spotish API

The Spotish API is documented [here](./spotishAPI.md)

### Caching Strategy

In the API, we have implemented a caching strategy to optimize performance and reduce latency for frequently accessed data.

We used validation caching for all the playlists endpoints as they implement most of the CRUD operations and are likely to change often when a user add or remove songs from a playlist.

As seen in class, we used `Last-Modified` header to indicate the last time the resource was modified. This allows the client to cache the response and only request the resource again if it has been modified since the last request. We use a simple approach to invalidate the cache for get all or get many requests, when a playlist is modified, the cache for all playlists is invalidated by updating a global index with the last modified time of the most recently modified playlist.

We used expiration caching for the musics endpoints as the musics data is less likely to change frequently. However, we have set different expiration times based on the type of data being requested.

For example, the 10 last listened musics and most listened musics endpoints have a shorter expiration times. Liked musics also have a shorter expiration time as users may frequently like or unlike musics.

However, endpoints to get all musics or get info about a specific music have longer expiration times as this data is less likely to change frequently.

### Query the API

If you wish to query the API directly, you must prefix your requests to all the endpoints with `/api`.

See [usage examples]() for more details.

## Get started

If you wish to try the project locally, follow these instructions:

First, clone the repository:

```bash
git clone git@github.com:Tigid0u/spotish.git
```

### Edit you `/etc/hosts` file

To simulate a real domain name locally, you need to edit your `/etc/hosts` file to add the following line:

```plaintext
127.0.0.1    spotish.test
127.0.0.1    traefik.spotish.test
```

### Edit the `.env` file

Copy the `.env.template` file located in the root of the project and rename it to `.env`.

Fill the `TRAEFIK_FULLY_QUALIFIED_DOMAIN_NAME` variable with `traefik.spotish.test` and the `APP_DOMAIN` variable with `spotish.test`.

Then fill the database credentials as follows:

- `POSTGRES_PASSWORD`: the password for the `postgres` superuser.
- `POSTGRES_USER_DATABASE`: the name of the database to create (default is `bdr`).
- `POSTGRES_USER_USERNAME`: the username of the user to create that will own the database (default is `bdr`.)
- `POSTGRES_USER_PASSWORD`: the password for the user to create.

### Create Docker Network

Create a Docker network that will be used by all the services to communicate with each other and for Traefik to route the traffic.

```bash
docker network create traefik_network
```

### Database

For the database we recommend using **PostgreSQL** and the `db-compose.yml` file located in the root of the project as it will automatically create the required database and user for you as well as populate it with some sample data.

To create it, run the following command in the root of the project:

```bash
docker compose -f db-compose.yml up -d
```

Just make sure you have the sql scripts located in the `sql_scripts/init` folder before running the command.

### Backend and frontend

To run the backend and frontend, you can use the `app-compose.yml` file located in the root of the project.

Just run the following command in the root of the project:

```bash
docker compose -f app-compose.yml up -d
```

### Traefik Reverse Proxy

Even if testing locally, you must use the Traefik reverse proxy to access the application. However, just be aware that HTTPS won't while testing locally.

To run Traefik, you can use the `traefik-compose.yml` file located in the root of the project.

```bash
docker compose -f traefik-compose.yml up -d
```

You should now be able to access the application at [http://spotish.test](http://spotish.test) and the Traefik dashboard at [http://traefik.spotish.test](http://traefik.spotish.test).

## Deployment Guide

**Virtual Machine for Hosting Docker Services**

This guide explains how to prepare a virtual machine to host Docker-based services. The instructions are written for **Ubuntu** (OS that we recommend for this project) with the help of Github Copilot.

### 1. Virtual Machine Prerequisites

You can obtain a virtual machine from **any cloud or hosting provider of your choice** (for example: a VPS or cloud VM).
The only assumption made in this guide is that the VM runs **Ubuntu**.

#### Required Open Ports

Ensure the following ports are **open and accessible** on the VM firewall and at the provider level:

- **22** – SSH access
- **80** – HTTP
- **443** – HTTPS

These ports are required for remote administration and for hosting web services.

---

### 2. SSH Access Using a Public Key

For security reasons, SSH access should be configured using **public key authentication** instead of passwords.

#### 2.1 Generate an SSH Key (Local Machine)

If you do not already have an SSH key, generate one on your local machine:

```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```

Press **Enter** to accept the default location and optionally set a passphrase.

This creates:

- A **private key** (keep it secret)
- A **public key** (safe to share)

#### 2.2 Add the Public Key to the VM

Copy the contents of your public key:

```bash
cat ~/.ssh/id_ed25519.pub
```

On the VM, add this key to the following file (usually it can be done via the cloud provider's console or SSH if password access is temporarily enabled):

```bash
~/.ssh/authorized_keys
```

Ensure correct permissions:

```bash
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

#### 2.3 Connect via SSH

You can now connect securely:

```bash
ssh username@your_vm_ip
```

---

### 3. Install Docker on Ubuntu

Docker must be installed directly on the VM to host containerized services.

Follow the [official Docker installation method for Ubuntu](https://docs.docker.com/engine/install/ubuntu/) to do so.

### 4. DNS Configuration Using Dynu

To expose your services publicly, a domain name must be linked to your VM’s public IP address.

You can obtain DNS records from **[https://www.dynu.com/](https://www.dynu.com/)**.

#### 4.1 Create an Account and Domain

1. Create an account on Dynu
2. Register or add an existing domain
3. Navigate to **DNS Management**

---

#### 4.2 Required DNS Records

At minimum, the following DNS records are required to link a domain to an IP address:

##### A Record (IPv4)

Links a domain or subdomain to the VM’s public IPv4 address.

| Type | Hostname       | Value (IP Address)  |
| ---- | -------------- | ------------------- |
| A    | example.com    | `YOUR_VM_PUBLIC_IP` |
| A    | \*.example.com | `YOUR_VM_PUBLIC_IP` |

After creating the DNS records, you can verify using `nslookup`:

```bash
nslookup example.com
```

### 5. Clone the project Repository

Clone the Spotish repository on your VM:

```bash
git clone https://github.com/Tigid0u/spotish.git
```

### 6. Fill `.env` file with your configuration

For the application to work you need to create a `.env` at the root of the project. For this you can copy the `.env.template` file, rename it to `.env` and fill it with your configuration (database credentials, etc.).

### 7. Deploy the Application

You can now deploy the application using Docker Compose.

But first, you need to create the Docker network that will be used by all the services to communicate with each other and for Traefik to route the traffic.

```bash
docker network create traefik_network
```

To deploy the database, run the following command in the root of the project:

```bash
docker compose -f db-compose.yml up -d
```

To deploy the backend and frontend, run the following command in the root of the project:

```bash
docker compose -f app-compose.yml up -d
```

To deploy the reverse proxy, run the following command in the root of the project:

```bash
docker compose -f traefik-compose.yml up -d
```

Then, you should be able to access the application using your domain name.

## Usage Examples

To explore the API, you can use tools like **Postman** or **cURL**. Here we will give some examples using cURL.

### Get a playlist by ID

```bash
curl -i http://localhost:8080/api/playlists/21
```

Where `21` is the ID of the playlist you want to retrieve. You can replace it with any valid playlist ID.

You should receive a response similar to this:

```http
HTTP/1.1 200 OK
Date: Sat, 10 Jan 2026 16:33:55 GMT
Content-Type: application/json
Last-Modified: 2026-01-10T17:33:55.971715010
Content-Length: 219

{"id":21,"name":"Ma playlist","description":"Test cache","creatorName":"amelie.paris","musics":[{"musicId":21,"title":"Believer","releaseDate":[2017,2,1],"duration":204,"genre":"Rock","creatorNames":"Imagine Dragons"}]}
```

### Get all playlists from a Creator

```bash
curl -i http://localhost:8080/api/playlists/user/amelie.paris
```

Where `amelie.paris` is the username of the creator whose playlists you want to retrieve. You can replace it with any valid username.

You should receive a response similar to this:

```http
HTTP/1.1 200 OK
Date: Sat, 10 Jan 2026 19:02:10 GMT
Content-Type: application/json
Last-Modified: 2026-01-10T20:02:10.432527161
Content-Length: 683

[{"id":2,"name":"Pop Mix","description":"Sélection pop moderne","creatorName":"amelie.paris","musics":[{"musicId":23,"title":"Yellow","releaseDate":[2000,6,26],"duration":260,"genre":"Pop","creatorNames":"Coldplay"},{"musicId":33,"title":"Shape of You","releaseDate":[2017,1,6],"duration":234,"genre":"Pop","creatorNames":"Ed Sheeran"}]},{"id":21,"name":"Ma playlist","description":"Test cache","creatorName":"amelie.paris","musics":[{"musicId":21,"title":"Believer","releaseDate":[2017,2,1],"duration":204,"genre":"Rock","creatorNames":"Imagine Dragons"},{"musicId":30,"title":"R U Mine?","r...
```
