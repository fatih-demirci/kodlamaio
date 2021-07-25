package com.kodlamaio.hrms.entities.conretes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sehirler")
public class City {
	
	@Id
	@Column(name="id")
	int id;

	@Column(name="sehir_isim",nullable = false, unique = true)
	String city;
	
	@JsonIgnore
	@OneToMany(mappedBy = "city" ,cascade = CascadeType.ALL)
	private List<JobAdvertisement> jobAdvertisements;
}
