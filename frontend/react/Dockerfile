FROM node:20-alpine
ARG api_base_url
WORKDIR /app
COPY package*.json .
RUN npm install -g npm@9.8.1 --silent
RUN npm i --legacy-peer-deps --silent
COPY . .
RUN echo "VITE_API_BASE_URL=${api_base_url}" > .env
EXPOSE 5173
CMD ["npm", "run", "dev"]