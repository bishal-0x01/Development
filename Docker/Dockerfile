FROM python

WORKDIR /app

COPY ./hello.py .

CMD ["python","-c","print('Hello from container')"]

ENTRYPOINT ["python","hello.py"]